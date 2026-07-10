package io.rsug.kartadsl;

import io.rsug.zatupka.Iconeer;
import io.rsug.zatupka.TptFragments;
import io.rsug.zatupka.TpzContainer;
import io.rsug.zatupka.XiObjParser;
import io.rsug.zatupka.allinone.*;
import io.rsug.zatupka.channel.Channel;
import io.rsug.zatupka.dir.Party;
import io.rsug.zatupka.dir.Service;
import io.rsug.zatupka.xiobj.Texts;
import io.rsug.zatupka.xiobj.XiObj;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
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

                switch (typeID) {
                    case "AllInOne":
                        AllInOne ico = xiObjParser.parseAllInOne(Files.newInputStream(pathDynamic));
                        Assertions.assertNotNull(ico.getVersion().toString());
                        Iconeer iconeer = new Iconeer(ico);
                        iconeer.linter();
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
        extractTpt(Paths.get("../XI71_ByScenarios.tpz"));
        extractTpt(Paths.get("../XI7_1_SAP_BASIS_7.50_SP_35.tpz"));
        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_BYD_CRM_ON_DEMAND_3.0.tpz"));
        extractTpt(Paths.get("../XI7_1_CS_DEMO.tpz"));
        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_directory-objs.tpz"));
        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_ENERGY.tpz"));
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
                    AllInOne aio = xiObjParser.parseAllInOne(xiObj.getContent().dynamicContent);
                    try {
                        Iconeer iconeer = new Iconeer(aio);
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
        System.out.println(t1);
    }

    @Test
    public void allinoneTests() throws Exception {
        XiObjParser xiObjParser = new XiObjParser();
        AllInOne ico1 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/ico1.xml")));
        Assertions.assertEquals(2, ico1.getConditions().getRDSCONDSHORT().size());
        renderIco(ico1);
        System.out.println(new Iconeer(ico1).linter());

        AllInOne ico2 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/ico2.xml")));
        Assertions.assertTrue(ico2.getConditions().getRDSCONDSHORT().isEmpty());
        System.out.println(new Iconeer(ico2).linter());

        AllInOne ico3 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/ico3.xml")));
        Binding binding3 = ico3.getReceiverConfigurations().getReceiverConfiguration().get(0).getInterfaceDeterminations().getInterfaceDetermination().get(0).getBinding();
        Assertions.assertEquals("so_timeout", binding3.getProperties().getProperty().get(0).getName());
        System.out.println(new Iconeer(ico3).linter());

        AllInOne ico4 = xiObjParser.parseAllInOne(Objects.requireNonNull(getClass().getResourceAsStream("/xiobj/AllInOne/ico4.xml")));
        Assertions.assertEquals(100, ico4.getVersion().intValue());
        System.out.println(new Iconeer(ico4).linter());

    }

    public void renderIco(AllInOne ico) throws IOException {
        Iconeer iconeer = new Iconeer(ico);
        iconeer.linter();
        String dot = iconeer.dot();
        OutputStream os = Files.newOutputStream(Paths.get("src/test/resources/ICo.uml"));
        IOUtils.write(dot, os, StandardCharsets.UTF_8);
        os.close();
    }
}
