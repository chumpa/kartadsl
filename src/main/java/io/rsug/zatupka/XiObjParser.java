package io.rsug.zatupka;

import io.rsug.zatupka.xiobj.XiObj;

import javax.xml.bind.*;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class XiObjParser {
    static final XMLInputFactory xif = XMLInputFactory.newFactory();
    static final String namespace = "urn:sap-com:xi";
    static StreamFilter filterContent = new StreamFilter() {
        @Override
        public boolean accept(XMLStreamReader reader) {
            if (reader.isStartElement()) { //|| reader.isEndElement()
                String localName = reader.getLocalName();
                String ns = reader.getNamespaceURI();
                boolean b = "content".equals(localName) && namespace.equals(ns);
                return !b;
            }
            return true;
        }
    };

    public final List<ValidationEvent> validationEvents = new LinkedList<>();

    public XiObj parse(InputStream is) throws JAXBException, XMLStreamException {
        JAXBContext jc = JAXBContext.newInstance(XiObj.class);
        XMLStreamReader xsr = xif.createXMLStreamReader(is);
        XMLStreamReader xsr2 = xif.createFilteredReader(xsr, filterContent);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setEventHandler((ValidationEventHandler) event -> {
            if (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
                validationEvents.add(event);
            }
            return true;
        });
        return (XiObj) unmarshaller.unmarshal(xsr2);
    }


}
