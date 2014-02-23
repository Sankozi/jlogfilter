package org.sankozi.jlogfilter.gui;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ConfigurationStore {
    private volatile Configuration configuration;

    //injected in setters
    ListProperty<String> emphasisedStacktracePackages;
    IntegerProperty logEntriesTableSize;

    @Inject @Named("configurationPath")
    Path configurationFilePath;

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
        logEntriesTableSize.set(getConfiguration().logEntriesTableSize);
        logEntriesTableSize.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                Configuration conf = getConfiguration();
                conf.logEntriesTableSize = number2.intValue();
                saveConfiguration(conf);
            }
        });
    }

    @Inject
    public void setEmphasisedStacktracePackages(@Named("emphasisedStacktraces") ListProperty<String> emphasisedStacktracePackages) {
        this.emphasisedStacktracePackages = emphasisedStacktracePackages;
        System.out.println("emphasised categories " + getConfiguration().emphasisedStacktraces);
        emphasisedStacktracePackages.set(FXCollections.observableArrayList(getConfiguration().emphasisedStacktraces));
        emphasisedStacktracePackages.addListener(new ChangeListener<ObservableList<String>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<String>> observableValue, ObservableList<String> strings, ObservableList<String> strings2) {
                Configuration conf = getConfiguration();
                conf.emphasisedStacktraces = strings2;
                saveConfiguration(conf);
            }
        });
    }
}
