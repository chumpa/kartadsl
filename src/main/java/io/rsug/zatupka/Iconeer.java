package io.rsug.zatupka;

import io.rsug.zatupka.allinone.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Iconeer {
    public final AllInOne ico;
    public final String fromParty, fromService, ifaceNS, ifaceName, toParty, toService;

    public final Map<String, String> namespaces = new LinkedHashMap<>();
    public final int noReceiverBehaviour;
    public final Map<String, ReceiverConfiguration> receiverMapById = new LinkedHashMap<>();
    public final ReceiverConfiguration receiverWhenNotFound;
    public final int version;

    public Iconeer(AllInOne allInOne, List<String> header) {
        ico = Objects.requireNonNull(allInOne);
        fromParty = (header != null && !header.isEmpty()) ? header.get(0) : null;
        fromService = (header != null && header.size() > 1) ? header.get(1) : null;
        ifaceNS = (header != null && header.size() > 2) ? header.get(2) : null;
        ifaceName = (header != null && header.size() > 3) ? header.get(3) : null;
        toParty = (header != null && header.size() > 4) ? header.get(4) : null;
        toService = (header != null && header.size() > 5) ? header.get(5) : null;

        version = ico.getVersion().intValue();
        for (NSM.Definition def : ico.getNamespaceMapping().getNSM().getDefinition()) {
            namespaces.put(def.getPrefix(), def.getUri());
        }
        noReceiverBehaviour = ico.getNoReceiverBehaviour() == null ? 0 : ico.getNoReceiverBehaviour().getIfNoReceiverFound().intValue();
        for (ReceiverConfiguration recvConf : ico.getReceiverConfigurations().getReceiverConfiguration()) {
            receiverMapById.put(recvConf.getReceiverId(), recvConf);
        }
        if (noReceiverBehaviour == 2) {
            receiverWhenNotFound = getReceiverWhenNotFound();
        } else {
            receiverWhenNotFound = null;
        }
    }

    public String linter() {
        if (noReceiverBehaviour < 0 || noReceiverBehaviour > 2) {
            throw new IllegalStateException("noReceiverBehaviour=" + noReceiverBehaviour);
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("version=" + version);
        pw.printf("%s|%s|{%s}%s|%s|%s\n", fromParty, fromService, ifaceNS, ifaceName, toParty, toService);
        pw.println("noReceiverBehaviour=" + noReceiverBehaviour);
        String s = receiverMapById.values().stream()
                .map(entry -> simpleReceiverAddress(entry.getReceiver()))
                .collect(Collectors.joining(", ", "", ""));
        pw.println(s);

        pw.flush();
        return sw.toString();
    }

    public String dot() {
        StringBuilder dot = new StringBuilder();
        String s;
        dot.append("@startdot\n");
        dot.append("digraph G {\n");
        dot.append("    rankdir=LR;                 // Горизонтальное расположение (слева-направо)\n" +
                "    bgcolor=\"#F5F5F5\";          // Светло-серый фон\n" +
                "    fontname=\"Arial\";\n" +
                "    fontsize=12;\n" +
                "    label=\"Схема обмена ICo\";\n" +
                "    labelloc=\"t\";               // Метка сверху\n" +
                "    fontcolor=\"#333333\";\n" +
                "    compound=true;              // Разрешить ребрам соединять кластеры\n");
        dot.append("    node [\n" +
                "        fontname=\"Arial\",\n" +
                "        fontsize=11,\n" +
                "        shape=box,\n" +
                "        style=\"rounded, filled\",\n" +
                "        fillcolor=\"#E8E8E8\",\n" +
                "        color=\"#666666\",\n" +
                "        penwidth=1.5\n" +
                "    ];\n" +
                "    edge [\n" +
                "        fontname=\"Arial\",\n" +
                "        fontsize=10,\n" +
                "        color=\"#555555\",\n" +
                "        penwidth=1.5,\n" +
                "        arrowsize=0.8\n" +
                "    ];\n");

        dot.append("  START [shape=circle, label=\"start\", fillcolor=\"#3498DB\", fontcolor=\"white\", width=0.6, fixedsize=true];\n");
        dot.append("subgraph receiver_determination {\nlabel=\"Определение получателя\";\n" +
                "        style=\"filled\";\n" +
                "        color=\"#27AE60\";\n" +
                "        bgcolor=\"#E9F7EF\";\n" +
                "        fontcolor=\"#1E8449\";\n" +
                "        fontname=\"Arial\";\n");

        for (String id : receiverMapById.keySet()) {
            ReceiverConfiguration rc = receiverMapById.get(id);
            s = simpleReceiverAddress(rc.getReceiver());
            String color = rc == receiverWhenNotFound ? ", fillcolor=\"red\"" : "";
            s = String.format("    %s[label=\"%s\"%s];", id, s, color);
            dot.append(s).append("\n");
        }
        dot.append("}\n");

        dot.append("}\n");
        dot.append("@enddot\n");
        return dot.toString();
    }

    ReceiverConfiguration getReceiverWhenNotFound() {
        if (noReceiverBehaviour == 0) {
            throw new IllegalStateException("noReceiverBehaviour(0, Error) has no predefined error receiver");
        }
        if (noReceiverBehaviour == 1) {
            throw new IllegalStateException("noReceiverBehaviour(1, Ignore) has no predefined error receiver");
        }
        String id = ico.getNoReceiverBehaviour().getDefaultReceiverId();
        return Objects.requireNonNull(receiverMapById.get(id));
    }

    // возвращает "PARTY|SERVICE" или "|SERVICE" для связки KO-экстракторов
    static String simpleReceiverAddress(Receiver icoReceiver) {
        Objects.requireNonNull(icoReceiver);
        TRDEXTRACTOR partyExtractor = icoReceiver.getPartyExtractor().getTRDEXTRACTOR();
        TRDEXTRACTOR serviceExtractor = icoReceiver.getServiceExtractor().getTRDEXTRACTOR();
        boolean koParty = TRDEXTRACTORTYPEENUM.KO.equals(partyExtractor.getTYPE()) && "xsd:string".equals(partyExtractor.getDATATYPE());
        boolean koService = TRDEXTRACTORTYPEENUM.KO.equals(serviceExtractor.getTYPE()) && "xsd:string".equals(serviceExtractor.getDATATYPE());
        if (koParty && koService) {
            return partyExtractor.getVALUE() + "|" + serviceExtractor.getVALUE();
        }
        throw new IllegalStateException();
    }

}
