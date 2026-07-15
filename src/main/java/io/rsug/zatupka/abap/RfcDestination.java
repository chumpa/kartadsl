package io.rsug.zatupka.abap;

import io.rsug.zatupka.SE16;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class RfcDestination {
    /**
     * I	RFC-соединение с сервером приложений с такой же БД
     * 3	RFC-соединение с ABAP-системой через TCP/IP
     * T	Запуск внешней программы через TCP/IP
     * L	Ссылочная запись (ссылка на другой адрес)
     * H	HTTP-соединение с ABAP-системой
     * G	HTTP-связь с внешней системой
     * W	RFC-соединение через WebSockets
     * X    Connections using ABAP Driver
     * <p>
     * См. также в абапе RFCOPT, FM RFC_READ_TCPIP_DESTINATION, RFC_READ_HTTP_DESTINATION
     */
    public final RfcDestinationEnum explainedType;
    public final char type;
    public final String name, docu;
    public final Map<String, String> options = new LinkedHashMap<>();

    public final String host, uname, I, D, M, G, g, s, U, programId;
    public final static String __RFCSERVER__ = "%%RFCSERVER%%";
    public final static String __SAPGUI__ = "%%SAPGUI%%";
    public final int port;
    public final boolean httpSecure;

    public RfcDestination(String name, char type, String rfcoptions, String docu) {
        this.type = type;
        this.name = name;
        this.docu = docu;

        if (rfcoptions != null && !rfcoptions.isEmpty()) {
            List<String> parts = List.of(Objects.requireNonNull(rfcoptions.split(",")));
            for (String p : parts) {
                String[] nv = p.split("=", 2);
                if (nv.length == 2 && !Objects.requireNonNull(nv[0]).isEmpty()) {
                    options.put(nv[0], nv[1]);
                } else {
                    throw new IllegalStateException();
                }
            }
            host = options.get("H");
            uname = options.get("U");
            D = options.get("D");
            M = options.get("M");
            G = options.get("G");
            g = options.get("g");
            s = options.get("s");
            U = options.get("U");
            programId = options.get("N");
            I = options.get("I");
            if (I != null && I.matches("[0-9]+")) {
                port = Integer.parseInt(I);
            } else {
                port = -1;
            }
        } else {
            host = null;
            uname = null;
            D = null;
            M = null;
            G = null;
            g = null;
            programId = null;
            I = null;
            s = null;
            U = null;
            port = -1;
        }
        boolean httpSecure = false;
        switch (type) {
            case 'I':
                explainedType = RfcDestinationEnum.INTERNAL;
                break;
            case '3':
                explainedType = RfcDestinationEnum.ABAP3;
                break;
            case 'T':
                if (isFrontend()) {
                    explainedType = RfcDestinationEnum.TFRONTEND;
                } else if (isInbound()) {
                    explainedType = RfcDestinationEnum.TINBOUND;
                } else if (host != null) {
                    explainedType = RfcDestinationEnum.TEXPLICIT;
                } else {
                    explainedType = RfcDestinationEnum.TAPPLICATION;
                }
                break;
            case 'L':
                explainedType = RfcDestinationEnum.LOCAL;
                break;
            case 'H':
                explainedType = RfcDestinationEnum.HTTPABAP;
                httpSecure = "Y".equals(this.s);
                break;
            case 'G':
                explainedType = RfcDestinationEnum.GHTTP;
                httpSecure = "Y".equals(this.s);
                break;
            case 'W':
                explainedType = RfcDestinationEnum.WEBSOCKETS;
                break;
            case 'X':
                explainedType = RfcDestinationEnum.XABAPDRIVER;
                break;
            default:
                throw new IllegalStateException();
        }
        this.httpSecure = httpSecure;
    }

    public boolean isInbound() {
        return type == 'T' && __RFCSERVER__.equals(host);
    }

    public boolean isFrontend() {
        return type == 'T' && __SAPGUI__.equals(host);
    }

    public String toPrettyString() {
        String s = String.format("RFC Destination(%s,%s)%s\t%s", type, explainedType, name, docu);
        switch (type) {
            case '3':
                s += String.format("\nHost=%s", host);
                break;
            case 'T':
                switch (explainedType) {
                    case TFRONTEND -> s += String.format("\nStart on Front-End workstation, Program=%s", programId);
                    case TINBOUND ->
                            s += String.format("\nRegistered Server Program, ProgramID=%s, gatewayHost=%s, gatewayService=%s", programId, G, g);
                    case TEXPLICIT -> s += String.format("\nStart on Explicit Host, Program=%s", programId);
                    case TAPPLICATION -> s += String.format("\nStart on Application server, Program=%s", programId);
                    default -> throw new IllegalStateException();
                }
                break;
            case 'H':
                s += String.format("\nHost=%s, port=%d, resource=%s, username=%s, https=%s", host, port, programId, U, httpSecure);
                break;
            case 'G':
                s += String.format("\nHost=%s, port=%d, resource=%s, username=%s, https=%s", host, port, programId, D, httpSecure);
                break;
            default:
                s += "\n" + options;
                break;
        }
        return s;
    }

    public static List<RfcDestination> parseSE16(SE16 rfcdes) {
        List<RfcDestination> rez = new LinkedList<>();
        for (List<String> row : rfcdes.rows) {
            Map<String, String> opts = rfcdes.getFields(row);
            String name = rfcdes.getField(row, "RFCDEST");
            String type = rfcdes.getField(row, "RFCTYPE");
            String options = Objects.toString(opts.get("RFCOPTIONS"), "")
                    + Objects.toString(opts.get("RFCOPTIONT"), "")
                    + Objects.toString(opts.get("RFCOPTIONU"), "")
                    + Objects.toString(opts.get("RFCOPTIONV"), "")
                    + Objects.toString(opts.get("RFCOPTION1"), "")
                    + Objects.toString(opts.get("RFCOPTION2"), "")
                    + Objects.toString(opts.get("RFCOPTION3"), "")
                    + Objects.toString(opts.get("RFCOPTION4"), "")
                    + Objects.toString(opts.get("RFCOPTION5"), "")
                    + Objects.toString(opts.get("RFCOPTION6"), "")
                    + Objects.toString(opts.get("RFCOPTION7"), "")
                    + Objects.toString(opts.get("RFCOPTION8"), "")
                    + Objects.toString(opts.get("RFCOPTION9"), "")
                    + Objects.toString(opts.get("RFCOPTIONA"), "")
                    + Objects.toString(opts.get("RFCOPTIONB"), "");
            String doc1 = Objects.toString(rfcdes.getField(row, "RFCDOC1"), "");
            String doc2 = Objects.toString(rfcdes.getField(row, "RFCDOC2"), "");
            String doc3 = Objects.toString(rfcdes.getField(row, "RFCDOC3"), "");

            RfcDestination dest = new RfcDestination(name, type.charAt(0), options, doc1 + doc2 + doc3);
            rez.add(dest);
        }
        return rez;
    }

    public static RfcDestination newInstanceG(String name, String docu, String url, String uname) throws URISyntaxException {
        URI uri = new URI(Objects.requireNonNull(url));
        String scheme = uri.getScheme();
        int port = uri.getPort();
        if (port == -1) {
            port = "https".equals(scheme) ? 443 : 80;
        }
        String path = uri.getPath();
        if (uri.getQuery() != null) path += "?" + uri.getQuery();

        String options = String.format("H=%s,I=%d,N=%s,s=%s,D=%s",
                uri.getHost(),
                port,
                path,
                "https".equals(scheme) ? "Y" : "N",
                uname
        );
        return new RfcDestination(name, 'G', options, docu);
    }

    public static RfcDestination newInstanceT(String name, String docu, String programId) {
        String options = String.format("N=%s", Objects.requireNonNull(programId));
        return new RfcDestination(name, 'T', options, docu);
    }

    public static RfcDestination newInstanceT(String name, String docu, String programId, String gatewayHost, String gatewayService) {
        String options = String.format("N=%s,G=%s,g=%s", Objects.requireNonNull(programId), Objects.requireNonNull(gatewayHost), Objects.requireNonNull(gatewayService));
        return new RfcDestination(name, 'T', options, docu);
    }
}
