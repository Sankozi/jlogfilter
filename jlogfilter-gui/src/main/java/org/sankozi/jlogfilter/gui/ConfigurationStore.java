package org.sankozi.jlogfilter.gui;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.IOException;
import java.nio.file.Path;

/**
 *
 */
public final class ConfigurationStore {
    private final Path configurationFilePath;

    private volatile Configuration configuration;

    @Inject
    public ConfigurationStore(@Named("configurationPath") Path configurationFilePath){
        this.configurationFilePath = configurationFilePath;
    }

    public Configuration getConfiguration() {
        if(configuration != null){
            return configuration;
        }
        Configuration ret;
        try {
            if(configurationFilePath.toFile().isFile()){
                ret = new Gson().fromJson(Files.toString(configurationFilePath.toFile(), Charsets.UTF_8), Configuration.class);
            } else {
                configurationFilePath.toFile().createNewFile();
                saveConfiguration(new Configuration());
                ret = getConfiguration();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    public void saveConfiguration(Configuration configuration){
        try {
            Files.write( new Gson().toJson(configuration), configurationFilePath.toFile(), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        configuration = null;
    }
}
