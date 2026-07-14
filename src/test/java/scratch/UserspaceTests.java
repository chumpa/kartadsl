package scratch;

import io.rsug.kartadsl.BackendABAP;
import io.rsug.kartadsl.IcoHeader;
import io.rsug.kartadsl.MWPO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserspaceTests {
    @Test
    public void abap() throws Exception {
        BackendABAP abd = new BackendABAP("ABD", 100, 110);
        MWPO pod = new MWPO("POD");
        MWPO po1 = new MWPO("PO1");

        pod.sld().newBS("ABD100", abd, 100);
        pod.sld().newBS("ABD110", abd, 110);
        pod.sld().newBS("INTEGRATION_ENGINE_JAVA_POD", pod);
        pod.sld().newBS("INTEGRATION_ENGINE_JAVA_PO1", po1);
        pod.sld().newBS("CRM_D");
        pod.sld().newBS("WMS_D");

        pod.dir().addAdapterFramework(po1);
        pod.dir().assignBS(null, "INTEGRATION_ENGINE_JAVA_POD");
        pod.dir().assignBS(null, "ABD100");
        pod.dir().assignBS(null, "CRM_D");

        pod.dir().createComponent(null, "FTP");
        pod.dir().createCC("|ABD100", "CC_XISender");
        pod.dir().createCC("|CRM_D", "CC_RESTReceiver_019Payments");
        pod.dir().createCC("|FTP", "CC_SFTPReceiver_018Invoices");

        pod.dir().createICo("|ABD100", "{urn:rsugio-kartadsl:erp}SI_019Payments_OutAsync", null)
                .assignCC("|ABD100|CC_XISender")
                .addReceiverExplicit("|CRM_D")
                .setOM1_1("|CRM_D", "{urn:rsugio-kartadsl:erp-crm:documents}OM_019Payments",
                        "{urn:rsugio-kartadsl:crm}SI_019Payments_InAsync"
                )
                .assignCC("{urn:rsugio-kartadsl:crm}SI_019Payments_InAsync", "|CRM_D|CC_RESTReceiver_019Payments")
                .activate();

        pod.dir().createICo("|ABD100", "{urn:rsugio-kartadsl:erp}SI_018Invoices_OutAsync", null)
                .assignCC("|ABD100|CC_XISender")
                .addReceiverExplicit("|FTP")
                .setOM1_1("|FTP", null, "{urn:rsugio-kartadsl:erp}SI_018Invoices_OutAsync")
                .assignCC("{urn:rsugio-kartadsl:erp}SI_018Invoices_OutAsync", "|FTP|CC_SFTPReceiver_018Invoices")
                .activate();

        new IcoHeader(null, "ABD100", "urn:rsugio-kartadsl:erp", "SI_019Payments_OutAsync")
                .setSenderChannel("CC_XISender");

    }

    @Test
    public void addressTests() throws Exception {
        Assertions.assertEquals("AA|BB", MWPO.extractComponentFromCC("AA|BB|CC"));
        Assertions.assertEquals("|BB", MWPO.extractComponentFromCC("|BB|CC"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MWPO.extractComponentFromCC("||CC");
        });
    }
}