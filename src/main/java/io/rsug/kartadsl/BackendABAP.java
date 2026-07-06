package io.rsug.kartadsl;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BackendABAP {
    public final String sid;
    public final List<Integer> mandts = new LinkedList<>();

    public BackendABAP(String sid, int... mandts) {
        this.sid = sid;
        for (int i : mandts) {
            this.mandts.add(i);
        }
    }

    public BackendABAP(String sid, List<Integer> mandts) {
        this.sid = sid;
        this.mandts.addAll(Objects.requireNonNull(mandts));
    }


}
