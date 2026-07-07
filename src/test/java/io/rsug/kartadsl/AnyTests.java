package io.rsug.kartadsl;

import io.rsug.zatupka.TptFragments;
import io.rsug.zatupka.TpzContainer;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
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
            System.out.println(ztp2.log);
        }
    }

    @Test
    public void ztpTest() throws Exception {
        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_BYD_CRM_ON_DEMAND_3.0.tpz"));
        extractTpt(Paths.get("../XI7_1_CS_DEMO.tpz"));
        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_directory-objs.tpz"));
        extractTpt(Paths.get("src/test/resources/tpz/XI7_1_ENERGY.tpz"));
        extractTpt(Paths.get("../XI71_ByScenarios.tpz"));
    }

    void extractTpt(Path tpz) throws IOException, XMLStreamException {
        Objects.requireNonNull(tpz);
        if (!Files.exists(tpz)) return;
        TpzContainer tpzContainer = new TpzContainer();
        if (tpzContainer.unzip(tpz)) {
            tpzContainer.makeFragments(true);
        }
        tpzContainer.clear();
    }


}
