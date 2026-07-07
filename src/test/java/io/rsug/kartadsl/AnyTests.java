package io.rsug.kartadsl;

import io.rsug.zatupka.TptFragments;
import io.rsug.zatupka.TpzContainer;
import io.rsug.zatupka.XiObjParser;
import io.rsug.zatupka.xiobj.XiObj;
import org.junit.jupiter.api.Test;

import javax.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                if (!xiObjParser.validationEvents.isEmpty()) {
                    //System.out.printf("Events when parse %s: %s\n", pathXiObj, xiObjParser.validationEvents);
                }
                System.out.printf("%s: typeID=%s\n", pathXiObj, xiObj.getIdInfo().getKey().getTypeID());
            }
        }
    }

    @Test
    public void ztpTest() throws Exception {
        extractTpt(Paths.get("../XI7_1_SAP_BASIS_7.50_SP_35.tpz"));
//        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_BYD_CRM_ON_DEMAND_3.0.tpz"));
//        extractTpt(Paths.get("../XI7_1_CS_DEMO.tpz"));
//        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_directory-objs.tpz"));
//        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_ENERGY.tpz"));
//        extractTpt(Paths.get("../XI71_ByScenarios.tpz"));
    }

    void extractTpt(Path tpz) throws IOException, XMLStreamException, JAXBException {
        Objects.requireNonNull(tpz);
        if (!Files.exists(tpz)) return;
        TpzContainer tpzContainer = new TpzContainer();
        if (tpzContainer.unzip(tpz)) {
            tpzContainer.extractFragments(false);
            for (Path pathXiObj : tpzContainer.listXiObjFiles) {
                XiObjParser xiObjParser = new XiObjParser();
                XiObj xiObj = xiObjParser.parse(Files.newInputStream(pathXiObj));
                String typeID = xiObj.getIdInfo().getKey().getTypeID();
                if (!xiObjParser.validationEvents.isEmpty()) {
                    System.out.printf("%s: typeID=%s (%s)\n", pathXiObj.getFileName(), typeID, xiObjParser.validationEvents);
                } else {
                    System.out.printf("%s: typeID=%s\n", pathXiObj.getFileName(), typeID);
                }
            }
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
                return !b;
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
}
