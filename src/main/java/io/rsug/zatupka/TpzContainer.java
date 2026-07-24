package io.rsug.zatupka;

import io.rsug.zatupka.xiobj.XiObj;
import jakarta.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TpzContainer {
    public final Path pathTmpDir;
    private Path pathTpt = null;
    public final Properties metadataProperties = new Properties();
    public final List<String> listTptFiles = new LinkedList<>();
    public final List<Path> listXiObjFiles = new LinkedList<>();

    public TpzContainer() throws IOException {
        this.pathTmpDir = Files.createTempDirectory("Zatupka_");
    }

    public boolean unzip(ZipInputStream zis) throws IOException {
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            String name = ze.getName();
            boolean isTpt = name.toLowerCase().endsWith(".tpt");
            boolean isMetadata = name.equals("Metadata.properties");
            boolean isDir = ze.isDirectory();
            if (!isDir && (isTpt || isMetadata)) {
                OutputStream os = Files.newOutputStream(pathTmpDir.resolve(name));
                IOUtils.copy(zis, os);
                os.close();
                if (isTpt) {
                    listTptFiles.add(name);
                } else {
                    metadataProperties.load(Files.newInputStream(pathTmpDir.resolve(name)));
                }
            }
            ze = zis.getNextEntry();
        }
        String transportFile = metadataProperties.getProperty("transportFile");
        if (!listTptFiles.isEmpty() && !metadataProperties.isEmpty() && listTptFiles.contains(transportFile)) {
            pathTpt = pathTmpDir.resolve(transportFile);
            return true;
        }
        return false;
    }

    public boolean unzip(Path tpz) throws IOException {
        ZipInputStream zis = new ZipInputStream(Files.newInputStream(tpz));
        boolean ok = unzip(zis);
        zis.close();
        return ok;
    }

    public void extractFragments(boolean alsoBinaries) throws XMLStreamException, IOException {
        TptFragments tptFragments = new TptFragments(Objects.requireNonNull(pathTpt));
        tptFragments.extractFragments(alsoBinaries);
        listXiObjFiles.addAll(tptFragments.xiObjects);
    }

    public void parseXiObjects() throws IOException, JAXBException, ParserConfigurationException, SAXException {
        for (Path p: listXiObjFiles) {
            XiObjParser xiObjParser = new XiObjParser();
            DOMSource src = XiObjParser.parseDOMSource(Objects.requireNonNull(Files.newInputStream(p)));
            try {
                XiObj xiObj = xiObjParser.parseXiObj(src);
                String typeID = xiObj.getIdInfo().getKey().getTypeID();
                String oid = xiObj.getIdInfo().getKey().getOid();
                Object o = xiObjParser.parseDynamicContent(xiObj);
                if (o==null) {
                    System.out.printf("Empty dynamic for typeID=%s\n", typeID);
                } else if (o instanceof String) {
                    System.out.printf("No handler for typeID=%s\n", typeID);
                }
                new XiObjFile(xiObj, typeID, oid, o, p);
            } catch (SAXParseException se) {
                // только ошибки валидации по схеме
                System.err.println(p);
                se.printStackTrace();
                throw se;
            }
        }
    }

    public void clear() throws IOException {
        List<Path> toDel = new LinkedList<>();
        for (Path p : Files.newDirectoryStream(pathTmpDir)) {
            toDel.add(p);
        }
        toDel.add(pathTmpDir);
        for (Path p : toDel) {
            Files.deleteIfExists(p);
        }
    }

}
