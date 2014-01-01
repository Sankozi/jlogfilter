package org.sankozi.logfilter.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.sankozi.logfilter.LogEntry;
import org.sankozi.logfilter.LogStore;

/**
 *
 */
public class MainPaneProvider implements Provider<Pane> {

    @Inject
    TableView<LogEntry> logEntryTable;

    @Inject
    LogStore logStore;

    @Override
    public Pane get() {
        BorderPane ret = new BorderPane();
        ret.getStylesheets().add("/style.css");

        HBox buttonPane = new HBox();
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logStore.clear();
            }
        });
        buttonPane.getChildren().addAll(clearButton);


        ret.setCenter(logEntryTable);
        ret.setTop(buttonPane);


        return ret;
    }
}
