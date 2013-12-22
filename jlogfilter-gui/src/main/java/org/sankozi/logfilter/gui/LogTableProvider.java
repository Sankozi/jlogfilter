package org.sankozi.logfilter.gui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.sankozi.logfilter.LogEntry;

import javax.inject.Provider;

/**
 *
 */
public class LogTableProvider implements Provider<TableView<LogEntry>> {

    private final TableColumn<LogEntry, String> messageColumn = new TableColumn<>("Message");   {
        messageColumn.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("message"));
    }
    private final TableColumn<LogEntry, String> categoryColumn = new TableColumn<LogEntry, String>("Category"); {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("category"));
    }

    @Override
    public TableView<LogEntry> get() {
        TableView<LogEntry> ret = new TableView<LogEntry>();
        ret.getColumns().addAll(messageColumn, categoryColumn);
        ret.getItems().add(new LogEntry("test message",  "test category"));
        ret.getItems().add(new LogEntry("test message 2","test category 2"));
        return ret;
    }
}
