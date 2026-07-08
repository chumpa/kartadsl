package io.rsug.zatupka;

import io.rsug.zatupka.xiobj.XiObj;
import org.w3c.dom.Node;

import javax.xml.bind.*;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

public class XiObjParser {
    static final String namespace = "urn:sap-com:xi";
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

    public XiObj parse(InputStream is) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(XiObj.class);
        XMLStreamReader xsr = xif.createXMLStreamReader(is);
        // xsr2 может понадобиться в будущем. Сейчас вместо него в Content:
        // @XmlAnyElement public org.w3c.dom.Element dynamicContent;
//        XMLStreamReader xsr2 = xif.createFilteredReader(xsr, filterContent);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setEventHandler(validationEventHandler);
        XiObj xiObj = (XiObj) unmarshaller.unmarshal(xsr);
        dynamicContent = elementToString(xiObj.getContent().dynamicContent);
        return xiObj;
    }

    String elementToString(org.w3c.dom.Element element) throws Exception {
        final Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(element);
        transformer.transform(source, result);
        return writer.toString();
    }
}
