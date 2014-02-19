package org.sankozi.jlogfilter.gui;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ConfigurationStore {
    private volatile Configuration configuration;


    IntegerProperty logEntriesTableSize;
    @Inject @Named("configurationPath")
    Path configurationFilePath;

    @PostConstruct
    public void init(){

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
                File confFile = configurationFilePath.toFile();
                if(!confFile.getParentFile().isDirectory() && !confFile.getParentFile().mkdirs()){
                    throw new RuntimeException("cannot create directory " + confFile.getParentFile().getAbsoluteFile());
                }
                if(!confFile.createNewFile()){
                    throw new RuntimeException("cannot create file " + confFile);
                }
                saveConfiguration(new Configuration());
                ret = getConfiguration();
            }

            logEntriesTableSize.set(ret.logEntriesTableSize);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        configuration = ret;
        return ret;
    }

    public void saveConfiguration(Configuration configuration){
        try {
            Files.write( new Gson().toJson(configuration), configurationFilePath.toFile(), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.configuration = null;
    }

    @Inject
    public void setLogEntriesTableSize(@Named("logEntriesTableSize") IntegerProperty logEntriesTableSize) {
        this.logEntriesTableSize = logEntriesTableSize;
        logEntriesTableSize.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                Configuration conf = getConfiguration();
                conf.logEntriesTableSize = number2.intValue();
                saveConfiguration(conf);
            }
        });
    }
}
