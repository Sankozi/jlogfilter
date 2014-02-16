package org.sankozi.jlogfilter.gui;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class ConfigurationStoreTest {

    @Test
    public void testIO() throws IOException {
        File testFile = File.createTempFile("testTempFile",".json");
        testFile.delete();
        assert !testFile.isFile();
        ConfigurationStore store = new ConfigurationStore(testFile.toPath());
        Configuration conf = store.getConfiguration();
        assert testFile.isFile();

        conf.detailPaneLocation = DetailPaneLocation.LEFT;
        store.saveConfiguration(conf);

        conf = store.getConfiguration();
        assert conf.detailPaneLocation == DetailPaneLocation.LEFT;
    }
}
