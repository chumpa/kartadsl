package io.rsug.kartadsl;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MWSLD {
    public final List<String> businessSystems = new LinkedList<>();

    public void newBS(String name, BackendABAP abap, int mandt) {
        MWPO.patternMatches(MWPO.PATTERN_COMPONENT, Objects.requireNonNull(name));
        if (!businessSystems.contains(name)) {
            businessSystems.add(name);
        }
    }

    public void newBS(String name, MWPO abap) {
        MWPO.patternMatches(MWPO.PATTERN_COMPONENT, Objects.requireNonNull(name));
        if (!businessSystems.contains(name)) {
            businessSystems.add(name);
        }
    }

    public void newBS(String name) {
        //3rd-party
        MWPO.patternMatches(MWPO.PATTERN_COMPONENT, Objects.requireNonNull(name));
        if (!businessSystems.contains(name)) {
            businessSystems.add(name);
        }
    }
}
