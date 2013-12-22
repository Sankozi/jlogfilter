package org.sankozi.logfilter.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.sankozi.logfilter.LogEntry;

/**
 *
 */
public class MainPaneProvider implements Provider<Pane> {

    @Inject
    TableView<LogEntry> logEntryTable;

    @Override
    public Pane get() {
        BorderPane ret = new BorderPane();
        ret.setCenter(logEntryTable);
        return ret;
    }
}
