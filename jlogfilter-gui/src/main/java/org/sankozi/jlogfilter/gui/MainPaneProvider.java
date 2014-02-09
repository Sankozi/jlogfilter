package org.sankozi.jlogfilter.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.sankozi.jlogfilter.LogEntry;
import org.sankozi.jlogfilter.LogStore;

import static org.sankozi.jlogfilter.gui.FontAwesomeIcons.*;

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

    @Inject @Named("storedEntriesSize")
    IntegerProperty storedEntriesSize;

    @Inject @Named("freeMemoryKiB")
    LongProperty freeMemory;

    @Inject @Named("totalMemoryKiB")
    LongProperty totalMemory;

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

        VBox configPane = new VBox();
        final VBox hiddenConfigPane = new VBox();
        HBox topButtonPane = new HBox();

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

        hiddenConfigPane.getChildren().addAll(HBoxBuilder.create()
                .children(detailLabel, noDetailButton, leftDetailButton, downDetailButton, rightDetailButton)
                .build());
        hiddenConfigPane.setVisible(false);
        hiddenConfigPane.setManaged(false);

        final ToggleButton expandButton = new ToggleButton(Character.toString(PLUS_SQUARE));
        expandButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                boolean newVisible = !hiddenConfigPane.isVisible();
                hiddenConfigPane.setVisible(newVisible);
                hiddenConfigPane.setManaged(newVisible);
                expandButton.setText(Character.toString(newVisible ? MINUS_SQUARE : PLUS_SQUARE));
            }
        });
        expandButton.getStyleClass().add("fontAwesome");

        configPane.getChildren().addAll(topButtonPane,
                    hiddenConfigPane
                );

        Label storedSizeLabel = new Label();
        storedSizeLabel.textProperty().bind(Bindings.format(" Stored %s entries", storedEntriesSize));

        Label memoryLabel = new Label();
        memoryLabel.textProperty().bind(Bindings.format(" Free memory %sMiB / %sMiB", freeMemory.divide(1024), totalMemory.divide(1024)));

        topButtonPane.getChildren().addAll(expandButton, clearButton, storedSizeLabel, memoryLabel);
        topButtonPane.setAlignment(Pos.BASELINE_LEFT);

        ret.setCenter(splitPane);
        ret.setTop(configPane);

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
