package io.rsug.kartadsl;

import java.util.List;
import java.util.Objects;

public class IcoHeader {
    public final String fromParty, fromService, ifaceNS, ifaceName, toParty, toService;
    public SenderConnectivity senderCC = null;

    public IcoHeader(String fromParty, String fromService, String ifaceNS, String ifaceName, String toParty, String toService) {
        this.fromParty = fromParty;
        this.fromService = fromService;
        this.ifaceNS = ifaceNS;
        this.ifaceName = ifaceName;
        this.toParty = toParty;
        this.toService = toService;
    }

    public IcoHeader(String fromParty, String fromService, String ifaceNS, String ifaceName) {
        this.fromParty = fromParty;
        this.fromService = fromService;
        this.ifaceNS = ifaceNS;
        this.ifaceName = ifaceName;
        this.toParty = null;
        this.toService = null;
    }

    public IcoHeader(List<String> header) {
        Objects.requireNonNull(header);
        fromParty = !header.isEmpty() ? header.get(0) : null;
        fromService = header.size() > 1 ? header.get(1) : null;
        ifaceNS = header.size() > 2 ? header.get(2) : null;
        ifaceName = header.size() > 3 ? header.get(3) : null;
        toParty = header.size() > 4 ? header.get(4) : null;
        toService = header.size() > 5 ? header.get(5) : null;
    }

    public SenderConnectivity setSenderChannel(String name) {
        Objects.requireNonNull(name);
        this.senderCC = new SenderConnectivity(name);
        return this.senderCC;
    }
}
