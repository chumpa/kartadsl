package io.rsug.zatupka;

import io.rsug.zatupka.allinone.AllInOne;
import io.rsug.zatupka.dir.Channel;
import io.rsug.zatupka.dir.Party;
import io.rsug.zatupka.dir.Service;
import io.rsug.zatupka.xiobj.XiObj;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import jakarta.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class XiObjParser {
    static final String namespace = "urn:sap-com:xi";
    static final SchemaFactory factory;
    static final Schema schemaAllInOne, schemaXiObj;
    static final javax.xml.validation.Validator validatorAllInOne, validatorXiObj;

    static {
        factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream is1 = XiObjParser.class.getResourceAsStream("/io.rsug.zatupka.xsd/AllInOne.xsd");
        InputStream is2 = XiObjParser.class.getResourceAsStream("/io.rsug.zatupka.xsd/NSM.xsd");
        InputStream is4 = XiObjParser.class.getResourceAsStream("/io.rsug.zatupka.xsd/xiObj.xsd");
        try {
            Document ds1 = parseDocument(Objects.requireNonNull(is1));
            Document ds2 = parseDocument(Objects.requireNonNull(is2));
            Document ds4 = parseDocument(Objects.requireNonNull(is4));
//            пример резолвера с LSInput, может пригодиться в сложных схемах
//            Map<String, Document> schemaMap = new HashMap<>();
//            schemaMap.put("AllInOne.xsd", ds1);
//            schemaMap.put("NSM.xsd", ds2);
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
            // ds2 идёт в массиве первым, это важно!
            schemaAllInOne = factory.newSchema(new DOMSource[]{new DOMSource(ds2), new DOMSource(ds1)});
            validatorAllInOne = schemaAllInOne.newValidator();
            schemaXiObj = factory.newSchema(new DOMSource[]{new DOMSource(ds4)});
            validatorXiObj = schemaXiObj.newValidator();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    final XMLInputFactory xif = XMLInputFactory.newFactory();
    final TransformerFactory tf = TransformerFactory.newInstance();
    private final StreamFilter filterContent = new StreamFilter() {
        @Override
        public boolean accept(XMLStreamReader reader) {
            if (reader.isStartElement() || reader.isEndElement()) {
                String localName = reader.getLocalName();
                String ns = reader.getNamespaceURI();
                boolean b = "content".equals(localName) && namespace.equals(ns);
//                return !b;
            }
            return true;
        }
    };
    //validationEventHandler накапливает события для последующего анализа
    private final ValidationEventHandler validationEventHandler = new ValidationEventHandler() {
        @Override
        public boolean handleEvent(ValidationEvent event) {
            if (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
                validationEvents.add(event);
            }
            return true;
        }
    };
    public final List<ValidationEvent> validationEvents = new LinkedList<>();
    public String dynamicContent = null;

    //TODO переделать на предварительную валидацию
    public XiObj parse(InputStream is) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(XiObj.class);
        XMLStreamReader xsr = xif.createXMLStreamReader(is);

        // DOMSource src = new DOMSource(parseDocument(is));
        // validatorXiObj.validate(src);

        // xsr2 может понадобиться в будущем. Сейчас вместо него в Content:
        // @XmlAnyElement public org.w3c.dom.Element dynamicContent;
        // XMLStreamReader xsr2 = xif.createFilteredReader(xsr, filterContent);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setEventHandler(validationEventHandler);
        XiObj xiObj = (XiObj) unmarshaller.unmarshal(xsr);
        dynamicContent = elementToString(xiObj.getContent().dynamicContent);
        return xiObj;
    }

    public String elementToString(org.w3c.dom.Element element) throws Exception {
        final Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(element);
        transformer.transform(source, result);
        return writer.toString();
    }

    public static Document parseDocument(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }

    public AllInOne parseAllInOne(InputStream is) throws JAXBException, IOException, SAXException, ParserConfigurationException {
        JAXBContext jc = JAXBContext.newInstance(AllInOne.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        // DOMSource позволяет пройтись по InputStream дважды - сперва валидация по схеме, затем JAXB
        // Если делать new StreamSource(is) то StreamSource читается лишь однажды
        DOMSource src = new DOMSource(parseDocument(is));
        validatorAllInOne.validate(src);
        AllInOne ico = (AllInOne) unmarshaller.unmarshal(src);
        Objects.requireNonNull(ico.getVersion());
        return ico;
    }

    public AllInOne parseAllInOne(org.w3c.dom.Element element) throws JAXBException, IOException, SAXException {
        JAXBContext jc = JAXBContext.newInstance(AllInOne.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        DOMSource src = new DOMSource(element);
        validatorAllInOne.validate(src);
        AllInOne ico = (AllInOne) unmarshaller.unmarshal(element);
        Objects.requireNonNull(ico.getVersion());
        return ico;
    }

    public Channel parseChannel(InputStream is) throws JAXBException, XMLStreamException {
        JAXBContext jc = JAXBContext.newInstance(Channel.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        XMLStreamReader xsr = xif.createXMLStreamReader(is);
        Channel cc = (Channel) unmarshaller.unmarshal(xsr);
        Objects.requireNonNull(cc);
        return cc;
    }

    public Party parseParty(InputStream is) throws JAXBException, XMLStreamException {
        JAXBContext jc = JAXBContext.newInstance(Party.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        XMLStreamReader xsr = xif.createXMLStreamReader(is);
        Party party = (Party) unmarshaller.unmarshal(xsr);
        Objects.requireNonNull(party);
        return party;
    }

    public Service parseService(InputStream is) throws JAXBException, XMLStreamException {
        JAXBContext jc = JAXBContext.newInstance(Service.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        XMLStreamReader xsr = xif.createXMLStreamReader(is);
        Service service = (Service) unmarshaller.unmarshal(xsr);
        Objects.requireNonNull(service);
        return service;
    }


}
