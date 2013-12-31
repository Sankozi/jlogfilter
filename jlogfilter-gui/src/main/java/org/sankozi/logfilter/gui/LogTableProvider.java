package org.sankozi.logfilter.gui;

import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.sankozi.logfilter.LogEntry;
import org.sankozi.logfilter.LogStore;

import javax.inject.Provider;

/**
 *
 */
public class LogTableProvider implements Provider<TableView<LogEntry>> {
    private final static SimpleStringProperty EMPTY_STRING_PROPERTY = new SimpleStringProperty("");

    @Inject
    LogStore logStore;

    private final TableColumn<LogEntry, String> messageColumn = new TableColumn<>("Message"); {
        messageColumn.setMinWidth(200);
        messageColumn.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("message"));
    }

    private final TableColumn<LogEntry, String> categoryColumn = new TableColumn<LogEntry, String>("Category"); {
        categoryColumn.setMinWidth(200);
        categoryColumn.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("category"));
    }

    private final TableColumn<LogEntry, String> levelColumn = new TableColumn<LogEntry, String>("Level"); {
        levelColumn.setMinWidth(100);
        levelColumn.setMaxWidth(100);
        levelColumn.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("level"));
    }

    private final TableColumn<LogEntry, String> stacktraceColumn = new TableColumn<LogEntry, String>("Stacktrace"); {
        stacktraceColumn.setMinWidth(100);
        stacktraceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LogEntry, String> cell) {
                String stacktrace = cell.getValue().getStacktrace();
                if(stacktrace.isEmpty()){
                    return EMPTY_STRING_PROPERTY;
                } else {
                    return new SimpleStringProperty(stacktrace.substring(0, stacktrace.indexOf('\n')));
                }
            }
        });
    }

    @Override
    public TableView<LogEntry> get() {
        final TableView<LogEntry> ret = new TableView<LogEntry>();
        ret.getColumns().addAll(levelColumn, messageColumn, categoryColumn, stacktraceColumn);
        ret.setMaxHeight(ret.getMinHeight());
        logStore.addChangeListener(new Runnable() {
            @Override
            public void run() {
                ret.getItems().clear();
                ret.getItems().addAll(logStore.getTop(50));
            }
        });
        return ret;
    }
}
