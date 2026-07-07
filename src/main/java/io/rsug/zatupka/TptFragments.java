package io.rsug.zatupka;

import org.apache.commons.io.IOUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;


public class TptFragments {
    record TptCatalogEntry(String type, int offset, int length) {
    }

    public final StringBuilder log = new StringBuilder();
    // Сперва идут 0x100000 байт, затем 8 байт CRC
    // 8 байт с конца TPT-файла это ссылка на начало XML-каталога, в котором есть ссылки на фрагменты
    final static int portion = 0x100000;
    private final Path pathTpt, pathNoCRC;
    public final List<Path> xiObjects = new LinkedList<>();

    public TptFragments(Path pathTpt) {
        this.pathTpt = pathTpt;
        this.pathNoCRC = pathTpt.resolveSibling(pathTpt.getFileName().toString() + ".nocrc");
    }

    public void extractFragments(boolean alsoBinaries) throws IOException, XMLStreamException {
        long size = Files.size(pathTpt);
        ByteBuffer map = readPath(pathTpt, (int) size);
        log.append(String.format("TPT %s, dirty size=%d\n", pathTpt, size));

        final OutputStream os = Files.newOutputStream(pathNoCRC);
        byte[] buffer = new byte[portion];
        while (map.remaining() > 0) {
            boolean readCrc = true;
            int splice = portion;
            if (map.remaining() <= splice) {
                splice = map.remaining();
                readCrc = false;
            }
            map.get(buffer, 0, splice);
            os.write(buffer, 0, splice);
            os.flush();
            int position = map.position();
            if (readCrc) {
                long crc = map.getLong();
                log.append(String.format("CRC=0x%X at position 0x%X\n", crc, position));
            }
        }
        os.close();

        // читаем чистый файл
        size = Files.size(pathNoCRC);
        map = readPath(pathNoCRC, (int) size);
        int catalog = (int) map.getLong((int) (size - 8)) + 8;
        byte[] xmlCatalog = new byte[Math.toIntExact(size - catalog - 8)];
        map.get(catalog, xmlCatalog);
        log.append(String.format("xmlcatalog: %s\n", new String(xmlCatalog)));

        final XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xr = xmlif.createXMLStreamReader(new ByteArrayInputStream(xmlCatalog));
        List<TptCatalogEntry> entries = new LinkedList<>();
        while (xr.hasNext()) {
            final int t = xr.next();
            if (t == XMLStreamConstants.START_ELEMENT && xr.getLocalName().equals("segment")) {
                String type = xr.getAttributeValue("", "type");
                String offset = xr.getAttributeValue("", "offset");
                String length = xr.getAttributeValue("", "length");
                TptCatalogEntry entry = new TptCatalogEntry(type, Integer.parseInt(offset), Integer.parseInt(length));
                entries.add(entry);
            }
        }
        xr.close();

        int index = 0;
        String nameExtracted;
        Path extracted;
        OutputStream eos;
        byte[] exbuf;
        for (TptCatalogEntry entry : entries) {
            int offset = entry.offset() + 8;
            int len = entry.length() - 8;
            switch (entry.type) {
                case "header":
                    offset += 8;
                    len -= 8;
                case "modelGeneralData":
                case "metaModel-Model":
                case "modelElement":
                    nameExtracted = String.format("%s_%d_%s.xml", pathTpt.getFileName(), index, entry.type);
                    extracted = pathTpt.resolveSibling(nameExtracted);
                    eos = Files.newOutputStream(extracted);
                    exbuf = new byte[len];
                    map.get(offset, exbuf);
                    eos.write(exbuf);
                    eos.close();
                    if (len > 0) {
                        // пробуем распарсить exbuf как xml, убеждаемся что нет битых символов
                        XMLStreamReader exr = xmlif.createXMLStreamReader(new ByteArrayInputStream(exbuf));
                        while (exr.hasNext()) {
                            exr.next();
                        }
                        exr.close();
                        log.append(String.format("'%s' is valid xml, self-check OK\n", nameExtracted));
                    } else {
                        log.append(String.format("'%s' is zero-length xml, no checks\n", nameExtracted));
                    }
                    if (entry.type.equals("modelElement")) {
                        xiObjects.add(extracted);
                    }
                    break;
                default:
                    if (!alsoBinaries) break;
                    nameExtracted = String.format("%s_%d_%s.bin", pathTpt.getFileName(), index, entry.type);
                    extracted = pathTpt.resolveSibling(nameExtracted);
                    eos = Files.newOutputStream(extracted);
                    if (len > 0) {
                        exbuf = new byte[len];
                        map.get(offset, exbuf);
                        eos.write(exbuf);
                    }
                    eos.close();
                    break;
            }
            index++;
        }
    }

    private static ByteBuffer readPath(Path path, int size) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(size);
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        IOUtils.read(fileChannel, bb);
        fileChannel.close();
        bb.flip();
        return bb;
    }

}
