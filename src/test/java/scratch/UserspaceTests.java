package scratch;

import io.rsug.kartadsl.BackendABAP;
import io.rsug.kartadsl.MWPO;
import org.junit.jupiter.api.Test;

public class UserspaceTests {
    @Test
    public void abap() throws Exception {
        BackendABAP abd = new BackendABAP("ABD", 100, 110);
        BackendABAP abt = new BackendABAP("ABT", 400, 410);
        BackendABAP abp = new BackendABAP("ABP", 400);

        MWPO pod = new MWPO("POD");
        MWPO po1 = new MWPO("PO1");
        MWPO pot = new MWPO("POT");
        MWPO po3 = new MWPO("PO3");
        MWPO pop = new MWPO("POP");
        MWPO po5 = new MWPO("PO5");
        MWPO po6 = new MWPO("PO6");
    }
}
