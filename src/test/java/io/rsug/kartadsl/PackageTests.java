package io.rsug.kartadsl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PackageTests {
    @Test
    public void parties() throws Exception {
        MWPO pod = new MWPO("POD");
        pod.sld().newBS("BS");
        pod.dir().addParty("A");
        pod.dir().assignBS("A", "BS");
        pod.dir().assignBS(null, "BS");
        pod.dir().createComponent(null, "FTP");
        pod.dir().createComponent("A", "FTP");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pod.dir().createComponent("B", "FTP");
        });
        Assertions.assertEquals(4, pod.dir().communicationComponents.size());
    }
}
