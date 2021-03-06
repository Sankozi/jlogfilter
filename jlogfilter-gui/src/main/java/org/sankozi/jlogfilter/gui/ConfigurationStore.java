package org.sankozi.jlogfilter.gui;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.sankozi.jlogfilter.Level;
import org.sankozi.jlogfilter.LogProducer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ConfigurationStore {
    private final static ObjectMapper JSON_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    static {
        JSON_MAPPER.setVisibilityChecker(JSON_MAPPER.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    private volatile Configuration configuration;

    //injected in setters
    ListProperty<String> emphasisedStacktracePackages;
    MapProperty<String, Level> storedMinimalLevel;
    IntegerProperty logEntriesTableSize;
    Property<String> emphasisedEntryText;
    ListProperty<LogProducer> logProducers;

    @Inject @Named("configurationPath")
    Path configurationFilePath;

    public Configuration getConfiguration() {
        if(configuration != null){
            return configuration;
        }
        Configuration ret;
        try {
            System.out.println("Trying to load configuration from path : '" + configurationFilePath + "' ...");
            if(configurationFilePath.toFile().isFile()){
                ret = JSON_MAPPER.readValue(Files.toString(configurationFilePath.toFile(), Charsets.UTF_8), Configuration.class);
                System.out.println("...configuration loaded successfully.");
            } else {
                File confFile = configurationFilePath.toFile();
                if(!confFile.getParentFile().isDirectory()){
                    System.out.print("...directory not found, creating...");
                    if(!confFile.getParentFile().mkdirs()) {
                        throw new RuntimeException("cannot create directory " + confFile.getParentFile().getAbsoluteFile());
                    }
                }
                if(!confFile.createNewFile()){
                    throw new RuntimeException("cannot create file " + confFile);
                }
                saveConfiguration(new Configuration());
                System.out.print("...created file with default configuration.");
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
            Files.write(JSON_MAPPER.writeValueAsString(configuration), configurationFilePath.toFile(), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.configuration = null;
    }

    private abstract class ConfigurationChangeListener<T> implements ChangeListener<T> {
        @Override
        public void changed(ObservableValue<? extends T> observableValue, T t, T t2) {
            Configuration conf = getConfiguration();
            changeConfiguration(conf, t2);
            saveConfiguration(conf);
        }
        protected abstract void changeConfiguration(Configuration conf, T newValue);
    }

    @Inject
    public void setStoredMinimalLevel(@Named("storedMinimalLevel") MapProperty<String, Level> storedMinimalLevel){
        this.storedMinimalLevel = storedMinimalLevel;
        storedMinimalLevel.set(FXCollections.observableMap(getConfiguration().storedMinimalLevel));
        storedMinimalLevel.addListener(new ConfigurationChangeListener<ObservableMap<String, Level>>() {
            @Override
            protected void changeConfiguration(Configuration conf, ObservableMap<String, Level> newValue) {
                conf.storedMinimalLevel = newValue;
            }
        });
    }

    @Inject
    public void setLogEntriesTableSize(@Named("logEntriesTableSize") IntegerProperty logEntriesTableSize) {
        this.logEntriesTableSize = logEntriesTableSize;
        logEntriesTableSize.set(getConfiguration().logEntriesTableSize);
        logEntriesTableSize.addListener(new ConfigurationChangeListener<Number>() {
            @Override
            protected void changeConfiguration(Configuration conf, Number newValue) {
                conf.logEntriesTableSize = newValue.intValue();
            }
        });
    }

    @Inject
    public void setEmphasisedStacktracePackages(@Named("emphasisedStacktraces") ListProperty<String> emphasisedStacktracePackages) {
        this.emphasisedStacktracePackages = emphasisedStacktracePackages;
        System.out.println("emphasised categories " + getConfiguration().emphasisedStacktraces);
        emphasisedStacktracePackages.set(FXCollections.observableArrayList(getConfiguration().emphasisedStacktraces));
        emphasisedStacktracePackages.addListener(new ConfigurationChangeListener<ObservableList<String>>() {
            @Override
            protected void changeConfiguration(Configuration conf, ObservableList<String> newValue) {
                conf.emphasisedStacktraces = newValue;
            }
        });
    }

    @Inject
    public void setEmphasisedEntryText(@Named("emphasisedEntryText") StringProperty emphasisedEntryText) {
        this.emphasisedEntryText = emphasisedEntryText;
        System.out.println("emphasised entry pattern " + getConfiguration().emphasisedEntryText);
        emphasisedEntryText.setValue(getConfiguration().emphasisedEntryText);
        emphasisedEntryText.addListener(new ConfigurationChangeListener<String>() {
            @Override
            protected void changeConfiguration(Configuration conf, String newValue) {
                conf.emphasisedEntryText = newValue;
            }
        });
    }

    @Inject
    public void setLogProducers(ListProperty<LogProducer> logProducers){
        this.logProducers = logProducers;
        System.out.println("log producers " + logProducers);
        logProducers.set(FXCollections.observableArrayList(getConfiguration().logProducers));
        logProducers.addListener(new ConfigurationChangeListener<ObservableList<LogProducer>>() {
            @Override
            protected void changeConfiguration(Configuration conf, ObservableList<LogProducer> newValue) {
                conf.logProducers = newValue;
            }
        });
    }
}
