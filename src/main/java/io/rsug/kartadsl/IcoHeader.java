package io.rsug.kartadsl;

import io.rsug.zatupka.allinone.*;
import io.rsug.zatupka.xiobj.*;

import java.util.*;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.misc.STMessage;

public class IcoHeader {
    public final String fromParty, fromService, ifaceNS, ifaceName, toParty, toService;
    public String senderCC = null, senderCCoid;
    private IcoReceiverDetermination receiverDetermination = null;
    private List<LnkRole> linksWithRoles = new LinkedList<>();
    private LnkRole routingProgram = null;
    public String description = null, masterL = null;

    public IcoHeader(String fromParty, String fromService, String ifaceNS, String ifaceName, String toParty, String toService) {
        this.fromParty = Objects.requireNonNull(fromParty);
        this.fromService = Objects.requireNonNull(fromService);
        this.ifaceNS = Objects.requireNonNull(ifaceNS);
        this.ifaceName = Objects.requireNonNull(ifaceName);
        this.toParty = Objects.requireNonNull(toParty);
        this.toService = Objects.requireNonNull(toService);
    }

    public IcoHeader(String fromParty, String fromService, String ifaceNS, String ifaceName) {
        this.fromParty = Objects.requireNonNull(fromParty);
        this.fromService = Objects.requireNonNull(fromService);
        this.ifaceNS = Objects.requireNonNull(ifaceNS);
        this.ifaceName = Objects.requireNonNull(ifaceName);
        this.toParty = "";
        this.toService = "";
    }

    public IcoHeader(XiObj xiObj, AllInOne allInOne) {
        Objects.requireNonNull(xiObj);
        Objects.requireNonNull(allInOne);
        List<String> header = Objects.requireNonNull(xiObj.getIdInfo().getKey().getElem());
        if (header.size() != 6) {
            throw new RuntimeException();
        }
        fromParty = Objects.requireNonNull(header.get(0));
        fromService = Objects.requireNonNull(header.get(1));
        ifaceNS = Objects.requireNonNull(header.get(2));
        ifaceName = Objects.requireNonNull(header.get(3));
        toParty = Objects.requireNonNull(header.get(4));
        toService = Objects.requireNonNull(header.get(5));

        this.masterL = xiObj.getGeneric().getTextInfo().getTextObj().getMasterL();
        List<Texts> texts = xiObj.getGeneric().getTextInfo().getTextObj().getTexts();
        Optional<Texts> optionalTextL = texts.stream().filter(t -> this.masterL.equals(t.getLang())).findFirst();
        if (optionalTextL.isPresent()) {
            if (!optionalTextL.get().getText().isEmpty()) {
                this.description = optionalTextL.get().getText().getFirst().getContent();
            }
        }
        this.setLinkRoles(xiObj.getGeneric().getLnks().getLnkRole());
        this.receiverDetermination = this.setSenderChannelFromLinks();
        for (NSM.Definition def : allInOne.getNamespaceMapping().getNSM().getDefinition()) {
            this.receiverDetermination.addPrefix(def.getPrefix(), def.getUri());
        }
        this.receiverDetermination.setNoReceiverBehaviour(allInOne);
        this.routingProgram = getLnkRoleUnique("RoutingProgram");
    }

    public IcoHeader setLinkRoles(List<LnkRole> listRoles) {
        linksWithRoles.clear();
        linksWithRoles.addAll(Objects.requireNonNull(listRoles));
        return this;
    }

    public IcoReceiverDetermination setSenderChannel(String name) {
        this.senderCC = Objects.requireNonNull(name);
        this.receiverDetermination = new IcoReceiverDetermination(this);
        return this.receiverDetermination;
    }

    public IcoReceiverDetermination setSenderChannelFromLinks() {
        LnkRole senderChannel = getLnkRoleUnique("senderChannel");
        Key key = Objects.requireNonNull(Objects.requireNonNull(senderChannel.getLnk(), "No <p1:lnk> found").getKey(), "No <p1:key> found");
        if (senderChannel.getKpos().intValue() == 0 && key.getElem().size() == 3) {
            return setSenderChannel(key.getElem().get(2));
        }
        throw new IllegalStateException("Cannot get senderChannel from //p1:generic/p1:lnks");
    }

    public LnkRole getLnkRoleUnique(String roleName) {
        Objects.requireNonNull(roleName);
        List<LnkRole> list = getLnkRoles(roleName);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            throw new RuntimeException();
        }
        return list.getFirst();
    }

    public List<LnkRole> getLnkRoles(String roleName) {
        //TODO change roleName from String to public enum, посмотреть в Карлутке перечисление
        Objects.requireNonNull(roleName);
        return this.linksWithRoles.stream().filter(t -> roleName.equals(t.getRole())).toList();
    }

    public String toHtml(String sourceSystem) {
        STGroup group = new STGroupDir("src/main/resources/templates", '$', '$');
        group.setListener(new STErrorListener() {
            @Override
            public void compileTimeError(STMessage msg) {
                throw new RuntimeException("compileTimeError: " + msg);
            }

            @Override
            public void runTimeError(STMessage msg) {
                throw new RuntimeException("runTimeError: " + msg);
            }

            @Override
            public void IOError(STMessage msg) {
                throw new RuntimeException(msg.cause);
            }

            @Override
            public void internalError(STMessage msg) {
                throw new RuntimeException(msg.cause);
            }
        });
        group.registerRenderer(String.class, new HtmlEscapeStringRenderer());
        ST st = group.getInstanceOf("ico");
        st.add("title", getAddress());
        st.add("header", this);
        st.add("content", "<p>Это содержимое моей главной страницы.</p>");
        st.add("sourceSystem", sourceSystem);

        return st.render();
    }

    public String getAddress() {
        if (toParty.isEmpty() && toService.isEmpty()) {
            return String.format("%s|%s|{%s}%s", fromParty, fromService, ifaceNS, ifaceName);
        } else {
            return String.format("%s|%s|{%s}%s|%s|%s", fromParty, fromService, ifaceNS, ifaceName, toParty, toService);
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder(getAddress());
        for (Map.Entry<String, String> en : receiverDetermination.prefixes.entrySet()) {
            s.append(String.format("\n\t%s:\"%s\"", en.getKey(), en.getValue()));
        }

        if (routingProgram != null) {
            Key key = routingProgram.getLnk().getKey();
            s.append(String.format("\n\trouting mapping: {%s}%s", key.getElem().get(1), key.getElem().get(0)));
        }

        Receiver receiver = receiverDetermination.getNotFoundReceiver();
        int code = receiverDetermination.getNotFoundBehaviour();
        if (receiver != null) {
            String party = extractValue(receiver.getPartyExtractor());
            String service = extractValue(receiver.getServiceExtractor());
            s.append(String.format("\n\tnot found behaviour: %d -> %s|%s", code, party, service));
        } else {
            s.append(String.format("\n\tnot found behaviour: %d", code));
        }
        return s.toString();
    }

    public static TRDEXTRACTORT trdextractortKO(String value) {
        TRDEXTRACTOR t = new TRDEXTRACTOR();
        t.setTYPE(TRDEXTRACTORTYPEENUM.KO);
        t.setDATATYPE("xsd:string");
        t.setVALUE(value);
        TRDEXTRACTORT tt = new TRDEXTRACTORT();
        tt.setTRDEXTRACTOR(t);
        return tt;
    }

    public static String extractValue(TRDEXTRACTORT tt) {
        TRDEXTRACTOR t = Objects.requireNonNull(tt.getTRDEXTRACTOR());
        if (t.getTYPE() == TRDEXTRACTORTYPEENUM.KO && "xsd:string".equals(t.getDATATYPE())) {
            return t.getVALUE();
        }
        throw new IllegalStateException();
    }


}
