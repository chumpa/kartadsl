package io.rsug.kartadsl;

import io.rsug.zatupka.*;
import io.rsug.zatupka.abap.RfcDestination;
import io.rsug.zatupka.allinone.AllInOne;
import io.rsug.zatupka.allinone.Binding;
import io.rsug.zatupka.channel.Channel;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class AnyTests {
    @Test
    public void tptTests() throws Exception {
        var s = Files.newDirectoryStream(Paths.get("temp/tpt"), "*.tpt");
        for (Path tpt : s) {
            var ztp2 = new TptFragments(tpt);
            ztp2.extractFragments(false);
            for (Path pathXiObj : ztp2.xiObjects) {
                XiObjParser xiObjParser = new XiObjParser();
                XiObj xiObj = xiObjParser.parse(Files.newInputStream(pathXiObj));
                String typeID = xiObj.getIdInfo().getKey().getTypeID();
                Texts texts = xiObj.getGeneric().getTextInfo().getTextObj().getTexts();
                String text = (texts == null) ? "" : texts.getText().getValue();
                if (text.length() > 40) text = text.substring(0, 40).trim();

                String dynamic = xiObjParser.dynamicContent;
                Path pathDynamic = pathXiObj.resolveSibling(pathXiObj.getFileName() + "." + typeID + ".xml");
                IOUtils.write(dynamic, Files.newOutputStream(pathDynamic), StandardCharsets.UTF_8);

                AllInOne ico;
                switch (typeID) {
                    case "AllInOne":
                        try {
                            ico = xiObjParser.parseAllInOne(Files.newInputStream(pathDynamic));
                            Iconeer iconeer = new Iconeer(ico, xiObj.getIdInfo().getKey().getElem());
                            String rez = iconeer.linter();
                            System.out.println(rez);
                        } catch (SAXParseException sax) {
                            String err = String.format("Validation error at %s (line %d, column %d)\n%s", pathDynamic, sax.getLineNumber(), sax.getColumnNumber(), sax.getMessage());
                            System.err.println(err);
                        }
                        break;
                    case "Channel":
                        Channel cc = xiObjParser.parseChannel(Files.newInputStream(pathDynamic));
                        Assertions.assertTrue(cc.getChannelDirection().matches("[OI]"));
                        break;
                    case "Party":           // могут быть интересные Agency
                        Party party = xiObjParser.parseParty(Files.newInputStream(pathDynamic));
                        Assertions.assertFalse(party.getPartyIdentifier().isEmpty());
                        break;
                    case "Service":         // минимум инфы - только BS или SR
                        Service service = xiObjParser.parseService(Files.newInputStream(pathDynamic));
                        Assertions.assertTrue(service.getServiceType().matches("BS|SR"));
                        break;
                    case "AlertRule":       // пока не парсим
                    case "DirectoryView":   // бесполезная динамика (нет внутри ничего)
                    case "DOCU":            // вся документация внутри <p1:texts>, нет отдельного объекта
                        break;
                    default:
                        System.err.println("Unexpected typeID: " + typeID);
                }

                if (!xiObjParser.validationEvents.isEmpty()) {
                    System.out.printf("%s: typeID=%s [%s] (%s)\n", pathXiObj.getFileName(), typeID, text, xiObjParser.validationEvents);
                } else {
                    System.out.printf("%s: typeID=%s [%s]\n", pathXiObj.getFileName(), typeID, text);
                }
            }
        }
    }

    @Test
    public void ztpTest() throws Exception {
        extractTpt(Paths.get("../XI7_1_CS_DEMO.tpz"));
//        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_directory-objs.tpz"));
//        extractTpt(Paths.get("../XI71_ByScenarios.tpz"));
//        extractTpt(Paths.get("../XI7_1_SAP_BASIS_7.50_SP_35.tpz"));
//        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_BYD_CRM_ON_DEMAND_3.0.tpz"));
//        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_ENERGY.tpz"));
    }

    void extractTpt(Path tpz) throws Exception {
        Objects.requireNonNull(tpz);
        if (!Files.exists(tpz)) return;
        System.out.println("*** " + tpz + " ***");
        TpzContainer tpzContainer = new TpzContainer();
        if (tpzContainer.unzip(tpz)) {
            tpzContainer.extractFragments(false);
            for (Path pathXiObj : tpzContainer.listXiObjFiles) {
                XiObjParser xiObjParser = new XiObjParser();
                XiObj xiObj = xiObjParser.parse(Files.newInputStream(pathXiObj));
                String typeID = xiObj.getIdInfo().getKey().getTypeID();
//                String dynamic = xiObjParser.dynamicContent;
                if (typeID.equals("AllInOne")) {
                    try {
                        AllInOne aio = xiObjParser.parseAllInOne(xiObj.getContent().dynamicContent);
                        Iconeer iconeer = new Iconeer(aio, xiObj.getIdInfo().getKey().getElem());
                        System.out.println("linter: " + iconeer.linter());
                    } catch (Exception e) {
                        System.err.println(pathXiObj);
                        throw e;
                    }
                }
//                if (!xiObjParser.validationEvents.isEmpty()) {
//                    System.out.printf("%s: typeID=%s (%s)\n", pathXiObj.getFileName(), typeID, xiObjParser.validationEvents);
//                } else {
//                    System.out.printf("%s: typeID=%s\n", pathXiObj.getFileName(), typeID);
//                }
            }
        } else {
            throw new IllegalStateException();
        }
        tpzContainer.clear();
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
        AllInOne ico1 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico1.xml")));
        Assertions.assertEquals(2, ico1.getConditions().getRDSCONDSHORT().size());
        renderIco(ico1);
        System.out.println(new Iconeer(ico1, null).linter());

        AllInOne ico2 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico2.xml")));
        Assertions.assertTrue(ico2.getConditions().getRDSCONDSHORT().isEmpty());
        System.out.println(new Iconeer(ico2, null).linter());

        AllInOne ico3 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico3.xml")));
        Binding binding3 = ico3.getReceiverConfigurations().getReceiverConfiguration().getFirst().getInterfaceDeterminations().getInterfaceDetermination().get(0).getBinding();
        Assertions.assertEquals("so_timeout", binding3.getProperties().getProperty().getFirst().getName());
        System.out.println(new Iconeer(ico3, null).linter());

        AllInOne ico4 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico4.xml")));
        Assertions.assertEquals(100, ico4.getVersion().intValue());
        System.out.println(new Iconeer(ico4, null).linter());

        AllInOne ico5 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico5.xml")));
        Assertions.assertTrue(new Iconeer(ico5, null).namespaces.isEmpty());
        AllInOne ico6 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico6.xml")));
        Assertions.assertTrue(new Iconeer(ico6, null).namespaces.isEmpty());
        Assertions.assertEquals(100, new Iconeer(ico6, null).version);
        AllInOne ico7 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/Ico7.xml")));
        Assertions.assertEquals(110, new Iconeer(ico7, null).version);
    }

    public void renderIco(AllInOne ico) throws IOException {
        Iconeer iconeer = new Iconeer(ico, null);
        iconeer.linter();
        String dot = iconeer.dot();
        OutputStream os = Files.newOutputStream(Paths.get("src/test/resources/ICo.uml"));
        IOUtils.write(dot, os, StandardCharsets.UTF_8);
        os.close();
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
