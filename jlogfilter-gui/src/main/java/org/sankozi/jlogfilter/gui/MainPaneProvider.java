package org.sankozi.jlogfilter.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.sankozi.jlogfilter.LogEntry;
import org.sankozi.jlogfilter.LogStore;
import org.sankozi.jlogfilter.util.NumberField;

import static org.sankozi.jlogfilter.gui.FontAwesomeIcons.*;

/**
 *
 */
public class MainPaneProvider implements Provider<Pane> {

    @Inject
    LogTable logEntryTable;

    @Inject
    DetailPane logEntryDetail;

    @Inject
    LogStore logStore;

    @Inject
    ConfigurationStore configurationStore;

    @Inject @Named("storedEntriesSize")
    IntegerProperty storedEntriesSize;

    @Inject @Named("freeMemoryKiB")
    LongProperty freeMemory;

    @Inject @Named("totalMemoryKiB")
    LongProperty totalMemory;

    @Inject @Named("logEntriesTableSize")
    IntegerProperty logEntriesTableSize;

    @Inject @Named("emphasisedStacktraces")
    ListProperty<String> emphasisedStacktracePackages;

    @Inject @Named("categoryTree")
    Node categoryTree;

    @Override
    public Pane get() {
        BorderPane ret = new BorderPane();
        Configuration configuration = configurationStore.getConfiguration();

        final SplitPane splitPane = new SplitPane();
        logEntryTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<LogEntry>() {
            @Override
            public void onChanged(Change<? extends LogEntry> change) {
                if(!logEntryTable.isRefreshing()){
                    while(change.next()){
                        if(change.wasAdded()){
                            logEntryDetail.setLogEntry(change.getList().get(0));
                        }
                    }
                }
            }
        });

        ret.getStylesheets().add("/style.css");

        VBox configPane = new VBox();

        HBox topButtonPane = new HBox();

        Label detailLabel = new Label(Character.toString(LIST));  //list
        detailLabel.getStyleClass().add("fontAwesome-button");

        ToggleGroup detailGroup = new ToggleGroup();
        ToggleButton noDetailButton = getToggleButton(configuration, CIRCLE, splitPane, detailGroup, DetailPaneLocation.NONE);
        ToggleButton leftDetailButton = getToggleButton(configuration, ARROW_CIRCLE_LEFT, splitPane, detailGroup, DetailPaneLocation.LEFT);
        ToggleButton downDetailButton = getToggleButton(configuration, ARROW_CIRCLE_DOWN, splitPane, detailGroup, DetailPaneLocation.BOTTOM);
        ToggleButton rightDetailButton = getToggleButton(configuration, ARROW_CIRCLE_RIGHT, splitPane, detailGroup, DetailPaneLocation.RIGHT);

        final VBox hiddenConfigPane = new VBox();
        hiddenConfigPane.setPadding(new Insets(5));
        hiddenConfigPane.setSpacing(5);
        hiddenConfigPane.getChildren().addAll(
                HBoxBuilder.create()
                        .children(detailLabel, noDetailButton, leftDetailButton, downDetailButton, rightDetailButton)
                        .build(),
                HBoxBuilder.create().alignment(Pos.BASELINE_LEFT).children(
                        new Label("Max rows in table:"),
                        NumberField.bidirectionalBinding(logEntriesTableSize)
                ).build(),
                categoryTree);
        hiddenConfigPane.setVisible(false);
        hiddenConfigPane.setManaged(false);

        configPane.getChildren().addAll(topButtonPane,
                    hiddenConfigPane
                );

        topButtonPane.getChildren().addAll(expandButton(hiddenConfigPane), clearButton(), getButton(),
                HBoxBuilder.create().alignment(Pos.CENTER_LEFT)
                        .fillHeight(true)
                        .children(storedSizeLabel(), memoryLabel()).build());
        topButtonPane.setAlignment(Pos.TOP_LEFT);

        ret.setCenter(splitPane);
        ret.setTop(configPane);

        return ret;
    }

    private Button getButton() {
        return ButtonBuilder.create().text("GC").onAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Runtime.getRuntime().gc();
                }
            }).styleClass("buttonOther").build();
    }

    private Button clearButton() {
        Button clearButton = new Button(Character.toString(TRASH_O)); //trash icon
        clearButton.getStyleClass().add("fontAwesome-button");
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logStore.deleteAll();
            }
        });
        clearButton.setTooltip(new Tooltip("Delete all stored log entries"));
        return clearButton;
    }

    private ToggleButton expandButton(final VBox hiddenConfigPane) {
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
        expandButton.getStyleClass().add("fontAwesome-button");
        return expandButton;
    }

    private Label memoryLabel() {
        Label memoryLabel = new Label();
        memoryLabel.textProperty().bind(Bindings.format(" Free memory %sMiB / %sMiB", freeMemory.divide(1024), totalMemory.divide(1024)));
        return memoryLabel;
    }

    private Label storedSizeLabel() {
        Label storedSizeLabel = new Label();
        storedSizeLabel.textProperty().bind(Bindings.format(" Stored %s entries", storedEntriesSize));
        return storedSizeLabel;
    }

    private ToggleButton getToggleButton(Configuration conf, char icon, final SplitPane splitPane, ToggleGroup detailGroup, final DetailPaneLocation dpl) {
                                         //final Orientation orientation, final Node... components) {
        ToggleButton button = new ToggleButton(Character.toString(icon));
        button.getStyleClass().add("fontAwesome-button");
        button.setToggleGroup(detailGroup);

        final Orientation orientation;
        final Node[] components;
        switch(dpl){
            case NONE:
                orientation = Orientation.VERTICAL;
                components = new Node[]{logEntryTable};
                break;
            case BOTTOM:
                orientation = Orientation.VERTICAL;
                components = new Node[]{logEntryTable,  logEntryDetail};
                break;
            case LEFT:
                orientation = Orientation.HORIZONTAL;
                components = new Node[]{logEntryDetail, logEntryTable};
                break;
            case RIGHT:
                orientation = Orientation.HORIZONTAL;
                components = new Node[]{logEntryTable,  logEntryDetail};
                break;
            default:
                throw new RuntimeException("unsupported " + dpl);
        }
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                splitPane.setOrientation(orientation);
                splitPane.getItems().setAll(components);
                Configuration conf = configurationStore.getConfiguration();
                conf.detailPaneLocation = dpl;
                configurationStore.saveConfiguration(conf);
            }
        });
        if(conf.detailPaneLocation == dpl){
            button.setSelected(true);
            splitPane.setOrientation(orientation);
            splitPane.getItems().setAll(components);
        }
        return button;
    }
}
