package org.sankozi.jlogfilter.gui;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import org.sankozi.jlogfilter.Level;
import org.sankozi.jlogfilter.LogEntry;
import org.sankozi.jlogfilter.LogStore;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.sql.Time;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.sankozi.jlogfilter.util.ImmutableObservableString.immutableString;

/**
 *
 */
public class LogTableProvider implements Provider<LogTable> {
    private final static ObservableStringValue EMPTY_STRING_PROPERTY = immutableString("");

    private final static Callback<TableColumn<LogEntry, String>,TableCell<LogEntry, String>> CENTER_ALIGNED_CELL_FACTORY
            = new Callback<TableColumn<LogEntry, String>,TableCell<LogEntry, String>>() {
        @Override
        public TableCell<LogEntry, String> call(TableColumn<LogEntry, String> tableColumn) {
            TableCell<LogEntry, String> ret = new TableCell<LogEntry, String>(){
                @Override
                protected void updateItem(String value, boolean empty) {
                    if(empty){
                        this.setText("");
                    } else {
                        this.setText(value);
                    }
                }
            };
            ret.setAlignment(Pos.CENTER);
            return ret;
        }
    };

    @Inject @Named("storedEntriesSize")
    IntegerProperty storedEntriesSize;

    @Inject @Named("freeMemoryKiB")
    LongProperty freeMemory;

    @Inject @Named("totalMemoryKiB")
    LongProperty totalMemory;

    @Inject @Named("logEntriesTableSize")
    IntegerProperty logEntriesTableSize;

    @Inject @Named("emphasisedEntryText")
    StringProperty emphasisedEntryText;

    @Inject
    LogStore logStore;

    volatile boolean logStoreEventsChanged = false;

    private final TableColumn<LogEntry, String> messageColumn = new TableColumn<>("Message"); {
        messageColumn.setSortable(false);
        messageColumn.setMinWidth(200);
        messageColumn.setCellFactory(CENTER_ALIGNED_CELL_FACTORY);
        messageColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LogEntry, String> cell) {
                String message = cell.getValue().getMessage();
                if(message.isEmpty()){
                    return EMPTY_STRING_PROPERTY;
                } else {
                    int newline = message.indexOf('\n');
                    if(newline != -1) {
                        return immutableString(message.substring(0, newline) + "[...]");
                    } else {
                        return immutableString(message);
                    }
                }
            }
        });
    }

    private final TableColumn<LogEntry, String> categoryColumn = new TableColumn<LogEntry, String>("Category"); {
        categoryColumn.setSortable(false);
        categoryColumn.setMinWidth(200);
        categoryColumn.setCellFactory(CENTER_ALIGNED_CELL_FACTORY);
        categoryColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LogEntry, String> cell) {
                return immutableString(cell.getValue().getCategory());
            }
        });
    }

    private final TableColumn<LogEntry, String> levelColumn = new TableColumn<LogEntry, String>("Level"); {
        levelColumn.setSortable(false);
        levelColumn.setMinWidth(60);
        levelColumn.setMaxWidth(60);
        levelColumn.setCellFactory(CENTER_ALIGNED_CELL_FACTORY);
        levelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LogEntry, String> cell) {
                return immutableString(cell.getValue().getLevel().name());
            }
        });
    }

    private final TableColumn<LogEntry, String> stacktraceColumn = new TableColumn<LogEntry, String>("Stack trace"); {
        stacktraceColumn.setSortable(false);
        stacktraceColumn.setMinWidth(100);
        stacktraceColumn.setCellFactory(CENTER_ALIGNED_CELL_FACTORY);
        stacktraceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LogEntry, String> cell) {
                String[] stacktrace = cell.getValue().getStacktrace();
                if(stacktrace.length == 0){
                    return EMPTY_STRING_PROPERTY;
                } else {
                    int newlineI = stacktrace[0].indexOf('\n');
                    if(newlineI == -1){
                        return immutableString(stacktrace[0] + " [...]");
                    } else {
                        return immutableString(stacktrace[0]);
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public LogTable get() {
        final LogTable ret = new LogTable();
        ret.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode() == KeyCode.DELETE){
                    logStore.delete(ret.getSelectionModel().getSelectedItems());
                }
            }
        });
        ret.getColumns().addAll(levelColumn, messageColumn, categoryColumn, stacktraceColumn);
        ret.setMaxHeight(ret.getMinHeight());
        logStore.addChangeListener(new Runnable() {
            @Override
            public void run() {
                logStoreEventsChanged = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        logStoreEventsChanged = true;
                    }
                });
            }
        });
        Timeline refreshLogTableTimeline = TimelineBuilder.create()
                .cycleCount(Timeline.INDEFINITE)
                .keyFrames(new KeyFrame(Duration.millis(250), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Runtime runtime = Runtime.getRuntime();
                        freeMemory.set(runtime.freeMemory() / 1024);
                        totalMemory.set(runtime.totalMemory() / 1024);
                        final List<LogEntry> entries = logStore.getTop(logEntriesTableSize.get());
                        storedEntriesSize.set(logStore.size());
                        if(logStoreEventsChanged){
                            logStoreEventsChanged = false;
                            ret.refresh(entries);
                        }
                    }
                }))
                .build();
        refreshLogTableTimeline.play();
        emphasisedEntryText.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                ret.emphasizePattern(newValue);
            }
        });
        ret.emphasizePattern(emphasisedEntryText.get());
        return ret;
    }
}
