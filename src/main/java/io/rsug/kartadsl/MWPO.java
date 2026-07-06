package io.rsug.kartadsl;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MWPO {
    public final String sid;
    private MWSLD sld = null;
    private MWPODir dir = null;

    public static final Pattern PATTERN_COMPONENT = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
    // Канал может начинаться с '_'
    public static final Pattern PATTERN_CC_SHORTNAME = Pattern.compile("[a-zA-Z0-9_]+");
    // адресация компонент вида [party]|component
    public static final Pattern PATTERN_ADDRESS = Pattern.compile("([a-zA-Z0-9_]*)?\\|[a-zA-Z0-9_]+");
    // в основном для экстракции из CC обратно в партию, компоненту, CC
    public static final Pattern PATTERN_CC_QN = Pattern.compile("(([a-zA-Z0-9_]*)?\\|([a-zA-Z0-9_]+))\\|([a-zA-Z0-9_]+)");


    public MWPO(String sid) {
        this.sid = sid;
    }

    public MWSLD sld() {
        if (sld == null) {
            sld = new MWSLD();
        }
        return sld;
    }

    public MWPODir dir() {
        if (dir == null && sld == null) {
            throw new IllegalStateException("No SLD defined");
        }
        if (dir == null) {
            dir = new MWPODir(this, sld);
        }
        return dir;
    }

    public static String requireAddress(String addr) {
        Objects.requireNonNull(addr);

        return addr;
    }

    public static String requireChannel(String channel) {
        Objects.requireNonNull(channel);
        return channel;
    }

    public static String requireIFQN(String ifqn) {
        Objects.requireNonNull(ifqn);
        return ifqn;
    }

    public static String makeAddress(String component) {
        return "|" + patternMatches(PATTERN_COMPONENT, Objects.requireNonNull(component));
    }

    public static String makeAddress(String party, String component) {
        if (party == null || party.isEmpty()) return makeAddress(component);
        return patternMatches(PATTERN_COMPONENT, Objects.requireNonNull(party)) + "|" + patternMatches(PATTERN_COMPONENT, Objects.requireNonNull(component));
    }

    public static String makeAddressCC(String address, String channel) {
        patternMatches(PATTERN_ADDRESS, Objects.requireNonNull(address));
        patternMatches(PATTERN_CC_SHORTNAME, Objects.requireNonNull(channel));
        String s = address + "|" + channel;
        patternMatches(PATTERN_CC_QN, s);
        return s;
    }

    public static String extractComponentFromCC(String channelAddress) {
        Matcher m = PATTERN_CC_QN.matcher(Objects.requireNonNull(channelAddress));
        if (!m.matches()) {
            throw new IllegalArgumentException("CC address not matched for " + PATTERN_CC_QN + ": " + channelAddress);
        }
        return m.group(1);
    }


    public static String patternMatches(Pattern pt, String test) {
        if (pt.matcher(test).matches())
            return test;
        throw new IllegalArgumentException("Pattern " + pt.pattern() + " not matched " + test);
    }

}
