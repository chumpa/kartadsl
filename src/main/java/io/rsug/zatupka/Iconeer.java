package io.rsug.zatupka;

import io.rsug.zatupka.allinone.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Iconeer {
    public final AllInOne ico;
    public final Map<String, String> prefixes = new LinkedHashMap<>();
    public final int noReceiverBehaviour;
    public final Map<String, ReceiverConfiguration> receiverMapById = new LinkedHashMap<>();
    public final ReceiverConfiguration receiverWhenNotFound;
    public final Set<String> qos = new HashSet<>();
    public final int version;

    public Iconeer(AllInOne allInOne) {
        ico = Objects.requireNonNull(allInOne);
        version = ico.getVersion().intValue();
        for (Definition def : ico.getNamespaceMapping().getNSM().getDefinition()) {
            prefixes.put(def.getPrefix(), def.getUri());
        }
        noReceiverBehaviour = ico.getNoReceiverBehaviour()==null ? 0 : ico.getNoReceiverBehaviour().getIfNoReceiverFound().intValue();
        for (ReceiverConfiguration recvConf : ico.getReceiverConfigurations().getReceiverConfiguration()) {
            receiverMapById.put(recvConf.getReceiverId(), recvConf);
            qos.add(recvConf.getQualityOfService());
        }
        if (noReceiverBehaviour==2) {
            receiverWhenNotFound = getReceiverWhenNotFound();
        } else {
            receiverWhenNotFound = null;
        }
    }

    public String linter() {
        if (noReceiverBehaviour < 0 || noReceiverBehaviour > 2) {
            throw new IllegalStateException("noReceiverBehaviour=" + noReceiverBehaviour);
        }
        if (qos.size()!=1) {
            throw new IllegalStateException("wrong QoS set=" + qos);
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("version=" + version);
        pw.println("noReceiverBehaviour=" + noReceiverBehaviour);
        pw.println("QoS=" + qos);
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

        for (String id: receiverMapById.keySet()) {
            ReceiverConfiguration rc = receiverMapById.get(id);
            s = simpleReceiverAddress(rc.getReceiver());
            String color = rc==receiverWhenNotFound ? ", fillcolor=\"red\"" : "";
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
        boolean koParty = "KO".equals(partyExtractor.getTYPE()) && "xsd:string".equals(partyExtractor.getDATATYPE());
        boolean koService = "KO".equals(serviceExtractor.getTYPE()) && "xsd:string".equals(serviceExtractor.getDATATYPE());
        if (koParty && koService) {
            return partyExtractor.getVALUE() + "|" + serviceExtractor.getVALUE();
        }
        throw new IllegalStateException();
    }

}
