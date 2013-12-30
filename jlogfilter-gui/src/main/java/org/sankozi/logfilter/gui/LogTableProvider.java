package org.sankozi.logfilter.gui;

import com.google.inject.Inject;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.sankozi.logfilter.LogEntry;
import org.sankozi.logfilter.LogStore;

import javax.inject.Provider;

/**
 *
 */
public class LogTableProvider implements Provider<TableView<LogEntry>> {
    @Inject
    LogStore logStore;

    private final TableColumn<LogEntry, String> messageColumn = new TableColumn<>("Message");   {
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

    @Override
    public TableView<LogEntry> get() {
        final TableView<LogEntry> ret = new TableView<LogEntry>();
        ret.getColumns().addAll(messageColumn, categoryColumn, levelColumn);
        logStore.addChangeListener(new Runnable() {
            @Override
            public void run() {
                ret.getItems().clear();
                ret.getItems().addAll(logStore.getTop(50));
            }
        });
//        ret.getItems().add(new LogEntry(Level.INFO, "test.category", "test message"));
//        ret.getItems().add(new LogEntry(Level.INFO, "test.category", "test message 2"));
        return ret;
    }
}
