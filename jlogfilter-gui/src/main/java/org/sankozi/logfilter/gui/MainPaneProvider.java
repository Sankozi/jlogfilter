package org.sankozi.logfilter.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.sankozi.logfilter.LogEntry;
import org.sankozi.logfilter.LogStore;

import static org.sankozi.logfilter.gui.FontAwesomeIcons.*;

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
        Button clearButton = new Button(Character.toString(TRASH_O)); //trash icon
        clearButton.getStyleClass().add("fontAwesome");
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logStore.clear();
            }
        });
        clearButton.setTooltip(new Tooltip("Delete all stored log entries"));

        Label detailLabel = new Label(Character.toString(LIST));  //list
        detailLabel.getStyleClass().add("fontAwesome");

        ToggleGroup detailGroup = new ToggleGroup();
        ToggleButton noDetailButton = getToggleButton(CIRCLE, splitPane, detailGroup, Orientation.HORIZONTAL, logEntryTable);
        ToggleButton leftDetailButton = getToggleButton(ARROW_CIRCLE_LEFT, splitPane, detailGroup, Orientation.HORIZONTAL, logEntryDetail, logEntryTable);
        ToggleButton downDetailButton = getToggleButton(ARROW_CIRCLE_DOWN, splitPane, detailGroup, Orientation.VERTICAL, logEntryTable, logEntryDetail);
        ToggleButton rightDetailButton = getToggleButton(ARROW_CIRCLE_RIGHT, splitPane, detailGroup, Orientation.HORIZONTAL, logEntryTable, logEntryDetail);
        rightDetailButton.setSelected(true);

        buttonPane.getChildren().addAll(clearButton, detailLabel, noDetailButton, leftDetailButton, downDetailButton, rightDetailButton);

        ret.setCenter(splitPane);
        ret.setTop(buttonPane);

        return ret;
    }

    private ToggleButton getToggleButton(char icon, final SplitPane splitPane, ToggleGroup detailGroup,
                                         final Orientation orientation, final Node... components) {
        ToggleButton downDetailButton = new ToggleButton(Character.toString(icon));
        downDetailButton.getStyleClass().add("fontAwesome");
        downDetailButton.setToggleGroup(detailGroup);
        downDetailButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                splitPane.setOrientation(orientation);
                splitPane.getItems().setAll(components);
            }
        });
        return downDetailButton;
    }
}
