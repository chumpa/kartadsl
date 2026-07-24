package io.rsug.kartadsl;

import io.rsug.zatupka.*;
import io.rsug.zatupka.abap.RfcDestination;
import io.rsug.zatupka.allinone.AllInOne;
import io.rsug.zatupka.allinone.Binding;
import io.rsug.zatupka.dir.Channel;
import io.rsug.zatupka.dir.Party;
import io.rsug.zatupka.dir.Service;
import io.rsug.zatupka.hmi.HmiRequest;
import io.rsug.zatupka.hmi.HmiResponse;
import io.rsug.zatupka.hmi.Instance;
import io.rsug.zatupka.xiobj.Texts;
import io.rsug.zatupka.xiobj.XiObj;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

import jakarta.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AnyTests {
    @Test
    public void ztpTest() throws Exception {
        extractTpt(Paths.get("../XI7_1_CS_DEMO.tpz"));
        extractTpt(Paths.get("../XI7_1_FOLDERS.tpz"));
//        extractTpt(Paths.get("../XI7_1_SAP_BASIS_7.50_SP_35.tpz"));
//        extractTpt(Paths.get("../XI71_ByScenarios.tpz"));
//        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_BYD_CRM_ON_DEMAND_3.0.tpz"));
        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_directory-objs.tpz"));
//        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_ENERGY.tpz"));
    }

    void extractTpt(Path tpz) throws Exception {
        Objects.requireNonNull(tpz);
        if (!Files.exists(tpz)) return;
        System.out.println("*** " + tpz + " ***");
        TpzContainer tpzContainer = new TpzContainer();
        if (tpzContainer.unzip(tpz)) {
            tpzContainer.extractFragments(false);
            // sourcesystem == hostpodci_POD_00
            String sourceSystem = tpzContainer.metadataProperties.getProperty("sourcesystem");
            // exportXiRelease == NW750EXT_35_REL
            String exportXiRelease = tpzContainer.metadataProperties.getProperty("exportXiRelease");
            tpzContainer.parseXiObjects();
//            AllInOne aio = xiObjParser.parseAllInOne(xiObj.getContent().dynamicContent);
//            IcoHeader icoHeader = new IcoHeader(xiObj, aio);
//            Path pathHtml = pathXiObj.resolveSibling(pathXiObj.getFileName().toString().replace(".xml", ".html"));
//            String s = icoHeader.toHtml(sourceSystem);
//            IOUtils.write(s, Files.newOutputStream(pathHtml), StandardCharsets.UTF_8);
        } else {
            throw new IllegalStateException();
        }
//        tpzContainer.clear();
    }

    @Test
    public void xobj() throws Exception {
        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(new FileInputStream("temp/tpt/M001.tpt_100_modelElement.xml"));
        XMLStreamReader xsr2 = xif.createFilteredReader(xsr, reader -> {
            if (reader.isStartElement()) { //|| reader.isEndElement()
                String localName = reader.getLocalName();
                String ns = reader.getNamespaceURI();
                boolean b = "content".equals(localName) && "urn:sap-com:xi".equals(ns);
//                return !b;
            }
            return true;
        });

        JAXBContext jc = JAXBContext.newInstance(XiObj.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setEventHandler((ValidationEventHandler) event -> {
            if (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
                System.err.println(event);
            }
            return true;
        });
        XiObj t1 = (XiObj) unmarshaller.unmarshal(xsr2);
        //t1.getContent().dynamicContent
        System.out.println(t1);
    }

    @Test
    public void allinoneTests() throws Exception {
        XiObjParser xiObjParser = new XiObjParser();
//        AllInOne ico1 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico1.xml")));
//        Assertions.assertEquals(2, ico1.getConditions().getRDSCONDSHORT().size());
//
//        AllInOne ico2 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico2.xml")));
//        Assertions.assertTrue(ico2.getConditions().getRDSCONDSHORT().isEmpty());
//
//        AllInOne ico3 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico3.xml")));
//        Binding binding3 = ico3.getReceiverConfigurations().getReceiverConfiguration().getFirst().getInterfaceDeterminations().getInterfaceDetermination().get(0).getBinding();
//        Assertions.assertEquals("so_timeout", binding3.getProperties().getProperty().getFirst().getName());
//
//        AllInOne ico4 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico4.xml")));
//        Assertions.assertEquals(100, ico4.getVersion().intValue());
//
//        AllInOne ico5 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico5.xml")));
//        AllInOne ico6 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico6.xml")));
//        AllInOne ico7 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico7.xml")));
    }

    public void renderIco(AllInOne ico) throws IOException {
//        Iconeer iconeer = new Iconeer(ico, null);
//        iconeer.linter();
//        String dot = iconeer.dot();
//        OutputStream os = Files.newOutputStream(Paths.get("src/test/resources/ICo.uml"));
//        IOUtils.write(dot, os, StandardCharsets.UTF_8);
//        os.close();
    }

    @Test
    public void hmiTests() throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Instance.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        Instance i00 = (Instance) unmarshaller.unmarshal(Objects.requireNonNull(getClass().getResourceAsStream("/hmi/00.xml")));

        HmiRequest r00 = new HmiRequest(i00);
        Assertions.assertEquals("74e735ef7c2211f1c02f00155d001a19", r00.ClientId);

        Instance i01 = (Instance) unmarshaller.unmarshal(Objects.requireNonNull(getClass().getResourceAsStream("/hmi/01.xml")));
        HmiResponse r01 = new HmiResponse(i01);
        Assertions.assertEquals("text/xml", r01.MethodOutput.ContentType);

        Instance i02 = (Instance) unmarshaller.unmarshal(Objects.requireNonNull(getClass().getResourceAsStream("/hmi/02.xml")));
        HmiRequest r02 = new HmiRequest(i02);
        Assertions.assertEquals("7.0", Objects.requireNonNull(r02.ClientLevel).Release);

        Instance i03 = (Instance) unmarshaller.unmarshal(Objects.requireNonNull(getClass().getResourceAsStream("/hmi/03.xml")));
        HmiResponse r03 = new HmiResponse(i03);
        Assertions.assertEquals("text/xml", r03.MethodOutput.ContentType);
    }

    @Test
    public void se16Tests() throws Exception {
        SE16 rfcdes = new SE16(getInputStream("/se16/RFCDES.txt"));
        List<RfcDestination> list = RfcDestination.parseSE16(rfcdes);
        for (RfcDestination d : list) {
            d.toPrettyString();
        }
        SE16 sxmsconfvl = new SE16(getInputStream("/se16/SXMSCONFVL.txt"));
        List<String> is_url = sxmsconfvl.getFieldDistinct(
                sxmsconfvl.selectAND(sxmsconfvl.rows, "AREA", "RUNTIME", "PARAM", "IS_URL"),
                "VALUE");
        Assertions.assertTrue(!is_url.isEmpty());
        Assertions.assertEquals("dest://PO_AAE_POD", is_url.get(0));
        SE16 sxmsconfdf = new SE16(getInputStream("/se16/SXMSCONFDF.txt"));
    }

    @Test
    public void rfcDestinationTests() throws Exception {
        RfcDestination g1 = RfcDestination.newInstanceG("SLD_CL_POD", null, "https://hostpod:44301/sld/cimom?azaza=zaza", "SLD_CL_POD_USER");
        System.out.println(g1.toPrettyString());
        RfcDestination t1 = RfcDestination.newInstanceT("SLD_UC", null, "SLD_UC");
        System.out.println(t1.toPrettyString());
        RfcDestination t2 = RfcDestination.newInstanceT("PO_IDOC_POD", "отправка IDOC", "XI_IDOC_ABD", "hostabd", "sapgw3300");
        System.out.println(t2.toPrettyString());
    }

    static InputStream getInputStream(String name) throws IOException {
        return Objects.requireNonNull(AnyTests.class.getResourceAsStream(name));
    }

}
