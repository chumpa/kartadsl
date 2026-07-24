package io.rsug.zatupka;

import io.rsug.zatupka.allinone.AlertRule;
import io.rsug.zatupka.allinone.AllInOne;
import io.rsug.zatupka.allinone.Attribs;
import io.rsug.zatupka.allinone.Valuemapping;
import io.rsug.zatupka.dir.Channel;
import io.rsug.zatupka.dir.Party;
import io.rsug.zatupka.dir.Service;
import io.rsug.zatupka.xiobj.XiObj;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Objects;

/**
 * Парсер /{urn:sap-com:xi}xiObj с возможным динамическим содержимым. Делает валидацию.
 */
public class XiObjParser {
    static final SchemaFactory factory;
    static final Schema schemaAllInOne, schemaXiObj, schemaHMI;
    static final javax.xml.validation.Validator validatorAllInOne, validatorXiObj, validatorHMI;
    private static final XMLInputFactory xif = XMLInputFactory.newFactory();
    private static final TransformerFactory tf = TransformerFactory.newInstance();

    static {
        factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream is1 = XiObjParser.class.getResourceAsStream("/io.rsug.zatupka.xsd/AllInOne.xsd");
        InputStream is2 = XiObjParser.class.getResourceAsStream("/io.rsug.zatupka.xsd/NSM.xsd");
        InputStream is4 = XiObjParser.class.getResourceAsStream("/io.rsug.zatupka.xsd/xiObj.xsd");
        try {
            // DOMSource удобен для многократного использования и взаимных ссылок
            DOMSource ds1 = parseDOMSource(Objects.requireNonNull(is1));
            DOMSource ds2 = parseDOMSource(Objects.requireNonNull(is2));
            DOMSource ds4 = parseDOMSource(Objects.requireNonNull(is4));
            // ds2 идёт в массиве первым, это важно!
            schemaAllInOne = factory.newSchema(new DOMSource[]{ds2, ds1});
            validatorAllInOne = schemaAllInOne.newValidator();
            schemaXiObj = factory.newSchema(ds4);
            validatorXiObj = schemaXiObj.newValidator();
            schemaHMI = null;
            validatorHMI = null;
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object parseDynamicContent(XiObj xiObj) throws JAXBException, IOException, SAXException {
        String typeID = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(xiObj).getIdInfo()).getKey()).getTypeID();
        org.w3c.dom.Element dynamic = xiObj.getContent().dynamicContent;
        switch (typeID) {
            // для этих объектов нет динамики
            case "DOCU", "DirectoryView":
                return new Object();
        }
        DOMSource src = new DOMSource(Objects.requireNonNull(dynamic, typeID));
        return switch (typeID) {
            case "AllInOne" -> parseAllInOne(src);
            case "Channel" -> parseChannel(src);
            case "Party" -> parseParty(src);
            case "Service" -> parseService(src);
            case "AlertRule" -> parseAlertRule(src);
            case "FOLDER" -> parseFolder(src);
            case "ValueMapping" -> parseValueMapping(src);
            default -> typeID;
        };
    }

    public XiObj parseXiObj(Source src) throws JAXBException, IOException, SAXException {
        validatorXiObj.validate(Objects.requireNonNull(src));
        JAXBContext jc = JAXBContext.newInstance(XiObj.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        XiObj xiObj = (XiObj) unmarshaller.unmarshal(src);
        return Objects.requireNonNull(xiObj);
    }

    public AllInOne parseAllInOne(Source src) throws JAXBException, IOException, SAXException {
        validatorAllInOne.validate(Objects.requireNonNull(src));
        JAXBContext jc = JAXBContext.newInstance(AllInOne.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        AllInOne ico = (AllInOne) unmarshaller.unmarshal(src);
        Objects.requireNonNull(ico.getVersion());
        return ico;
    }

    public Channel parseChannel(Source src) throws JAXBException {
        Objects.requireNonNull(src);
        JAXBContext jc = JAXBContext.newInstance(Channel.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Channel cc = (Channel) unmarshaller.unmarshal(src);
        return Objects.requireNonNull(cc);
    }

    public Party parseParty(Source src) throws JAXBException {
        Objects.requireNonNull(src);
        JAXBContext jc = JAXBContext.newInstance(Party.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Party party = (Party) unmarshaller.unmarshal(src);
        return Objects.requireNonNull(party);
    }

    public Service parseService(Source src) throws JAXBException {
        Objects.requireNonNull(src);
        JAXBContext jc = JAXBContext.newInstance(Service.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Service service = (Service) unmarshaller.unmarshal(src);
        return Objects.requireNonNull(service);
    }

    public AlertRule parseAlertRule(Source src) throws JAXBException {
        Objects.requireNonNull(src);
        JAXBContext jc = JAXBContext.newInstance(AlertRule.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        AlertRule alertRule = (AlertRule) unmarshaller.unmarshal(src);
        return Objects.requireNonNull(alertRule);
    }

    public Attribs parseFolder(Source src) throws JAXBException {
        Objects.requireNonNull(src);
        JAXBContext jc = JAXBContext.newInstance(Attribs.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Attribs folder = (Attribs) unmarshaller.unmarshal(src);
        return Objects.requireNonNull(folder);
    }

    public Valuemapping parseValueMapping(Source src) throws JAXBException {
        Objects.requireNonNull(src);
        JAXBContext jc = JAXBContext.newInstance(Valuemapping.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Valuemapping valuemapping = (Valuemapping) unmarshaller.unmarshal(src);
        return Objects.requireNonNull(valuemapping);
    }

    public static String elementToString(org.w3c.dom.Element element) throws TransformerException {
        final Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(element);
        transformer.transform(source, result);
        return writer.toString();
    }

    public static DOMSource parseDOMSource(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        return new DOMSource(parseDocument(inputStream));
    }

    public static Document parseDocument(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }

    /**
     * Пример кода фильтрации при парсинге. Использовался первоначально, затем
     * не используется, так как валидация по схеме надёжнее
     //    private final StreamFilter filterContent = new StreamFilter() {
     //        @Override
     //        public boolean accept(XMLStreamReader reader) {
     //            if (reader.isStartElement() || reader.isEndElement()) {
     //                String localName = reader.getLocalName();
     //                String ns = reader.getNamespaceURI();
     //                boolean b = "content".equals(localName) && namespace.equals(ns);
     ////                return !b;
     //            }
     //            return true;
     //        }
     //    };
     //    validationEventHandler накапливает события для последующего анализа
     //    private final ValidationEventHandler validationEventHandler = new ValidationEventHandler() {
     //        @Override
     //        public boolean handleEvent(ValidationEvent event) {
     //            if (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
     //                validationEvents.add(event);
     //            }
     //            return true;
     //        }
     //    };
     //    final List<ValidationEvent> validationEvents = new LinkedList<>();
     // DOMSource src = new DOMSource(parseDocument(is));
     // validatorXiObj.validate(src);

     // xsr2 может понадобиться в будущем. Сейчас вместо него в Content:
     // XMLStreamReader xsr2 = xif.createFilteredReader(xsr, filterContent);
     Unmarshaller unmarshaller = jc.createUnmarshaller();
     unmarshaller.setEventHandler(validationEventHandler);


     //            пример резолвера xsd-ссылок с LSInput, может пригодиться в сложных схемах
     //            Map<String, Document> schemaMap = new HashMap<>();
     //            schemaMap.put("AllInOne.xsd", ds1);
     //            schemaMap.put("NSM.xsd", ds2);
     //            SchemaFactory factory = SchemaFactory.newInstance();
     //            factory.setResourceResolver(new LSResourceResolver() {
     //                @Override
     //                public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
     //                    // systemId — это имя файла, который импортируется
     //                    if (schemaMap.containsKey(systemId)) {
     //                        Document doc = schemaMap.get(systemId);
     //                        DOMImplementationLS lsImpl = (DOMImplementationLS) doc.getImplementation();
     //                        return lsImpl.createLSInput();
     //                    }
     //                    return null;
     //                }
     //            });


     */

}
