package org.sankozi.jlogfilter.gui;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.sankozi.jlogfilter.Level;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 */
public class ConfigurationStore {
    private final static GsonBuilder GSON_BUILDER = new GsonBuilder().setPrettyPrinting();

    private volatile Configuration configuration;

    //injected in setters
    ListProperty<String> emphasisedStacktracePackages;
    MapProperty<String, Level> storedMinimalLevel;
    IntegerProperty logEntriesTableSize;
    Property<String> emphasisedEntryText;

    @Inject @Named("configurationPath")
    Path configurationFilePath;

    private static Gson getGson() {
        return GSON_BUILDER.create();
    }

    public Configuration getConfiguration() {
        if(configuration != null){
            return configuration;
        }
        Configuration ret;
        try {
            System.out.println("Trying to load configuration from path : '" + configurationFilePath + "' ...");
            if(configurationFilePath.toFile().isFile()){
                ret = getGson().fromJson(Files.toString(configurationFilePath.toFile(), Charsets.UTF_8), Configuration.class);
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
            Files.write(getGson().toJson(configuration), configurationFilePath.toFile(), Charsets.UTF_8);
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
        System.out.println("emphasised categories " + getConfiguration().emphasisedEntryText);
        emphasisedEntryText.setValue(getConfiguration().emphasisedEntryText);
        emphasisedEntryText.addListener(new ConfigurationChangeListener<String>() {
            @Override
            protected void changeConfiguration(Configuration conf, String newValue) {
                conf.emphasisedEntryText = newValue;
            }
        });
    }
}
