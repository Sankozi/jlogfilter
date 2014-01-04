package org.sankozi.logfilter.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
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
    DetailPane logEntryDetail;

    @Inject
    LogStore logStore;

    @Override
    public Pane get() {
        BorderPane ret = new BorderPane();

        final SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().setAll(logEntryTable, logEntryDetail);
        logEntryTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<LogEntry>() {
            @Override
            public void onChanged(Change<? extends LogEntry> change) {
                if(change.getList().isEmpty()){
                    logEntryDetail.setLogEntry(null);
                } else {
                    logEntryDetail.setLogEntry(change.getList().get(0));
                }
            }
        });

        ret.getStylesheets().add("/style.css");

        HBox buttonPane = new HBox();
        Button clearButton = new Button("\uf014"); //trash icon
        clearButton.getStyleClass().add("fontAwesome");
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logStore.clear();
            }
        });
        clearButton.setTooltip(new Tooltip("Delete all stored log entries"));

        Label detailLabel = new Label("\uf03a");  //list
        detailLabel.getStyleClass().add("fontAwesome");

        ToggleGroup detailGroup = new ToggleGroup();
        final ToggleButton noDetailButton = new ToggleButton("\uf111");
        noDetailButton.getStyleClass().add("fontAwesome");
        noDetailButton.setToggleGroup(detailGroup);
        noDetailButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                splitPane.getItems().setAll(logEntryTable);
            }
        });

        ToggleButton leftDetailButton = new ToggleButton("\uf0a8");
        leftDetailButton.getStyleClass().add("fontAwesome");
        leftDetailButton.setToggleGroup(detailGroup);
        leftDetailButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                splitPane.setOrientation(Orientation.HORIZONTAL);
                splitPane.getItems().setAll(logEntryDetail, logEntryTable);
            }
        });

        ToggleButton downDetailButton = new ToggleButton("\uf0ab");
        downDetailButton.getStyleClass().add("fontAwesome");
        downDetailButton.setToggleGroup(detailGroup);
        downDetailButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                splitPane.setOrientation(Orientation.VERTICAL);
                splitPane.getItems().setAll(logEntryTable, logEntryDetail);
            }
        });;

        ToggleButton rightDetailButton = new ToggleButton("\uf0a9");
        rightDetailButton.getStyleClass().add("fontAwesome");
        rightDetailButton.setToggleGroup(detailGroup);
        rightDetailButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                splitPane.setOrientation(Orientation.HORIZONTAL);
                splitPane.getItems().setAll(logEntryTable, logEntryDetail);
            }
        });;
        rightDetailButton.setSelected(true);

        buttonPane.getChildren().addAll(clearButton, detailLabel, noDetailButton, leftDetailButton, downDetailButton, rightDetailButton);

        ret.setCenter(splitPane);
        ret.setTop(buttonPane);

        return ret;
    }
}
