package org.sankozi.jlogfilter.gui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.sankozi.jlogfilter.Level;
import org.sankozi.jlogfilter.LogEntry;
import org.sankozi.jlogfilter.LogStore;
import org.sankozi.jlogfilter.util.NumberField;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.sankozi.jlogfilter.gui.FontAwesomeIcons.*;
import static org.sankozi.jlogfilter.gui.GuiUtils.*;

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

    @Inject @Named("emphasisedEntryText")
    StringProperty emphasisedEntryText;

    @Inject @Named("categoryTree")
    Node categoryTree;

    @Inject
    LogProducerConfigurationPane logProducerConfigurationPane;

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

        final Node hiddenConfigPane = TabPaneBuilder.create().tabs(
                tab("GUI", VBoxBuilder.create()
                                .padding(new Insets(5))
                                .spacing(5)
                                .children(
                                        HBoxBuilder.create()
                                                .children(detailLabel, noDetailButton, leftDetailButton, downDetailButton, rightDetailButton)
                                                .build(),
                                        HBoxBuilder.create().alignment(Pos.BASELINE_LEFT).children(
                                                new Label("Max rows in table:"),
                                                NumberField.bidirectionalBinding(logEntriesTableSize)
                                        )
                                        .build()
                                ).build()
                ),
                tab("Category filters", categoryTree),
                tab("Logs", logProducerConfigurationPane)
        ).build();
        hiddenConfigPane.setVisible(false);
        hiddenConfigPane.setManaged(false);

        configPane.getChildren().addAll(topButtonPane, hiddenConfigPane);

        topButtonPane.getChildren().addAll(expandButton(hiddenConfigPane), clearButton(),
                clearLowerThanButton(Level.ERROR, FontAwesomeIcons.ERROR),
                clearLowerThanButton(Level.WARN, FontAwesomeIcons.WARNING),
                clearLowerThanButton(Level.INFO, FontAwesomeIcons.INFO),
                clearLowerThanButton(Level.DEBUG, FontAwesomeIcons.BUG),gcButton(),
                HBoxBuilder.create().alignment(Pos.CENTER_LEFT)
                        .fillHeight(true)
                        .children(emphasizedPatternField(), storedSizeLabel(), memoryLabel()).build());
        topButtonPane.setAlignment(Pos.TOP_LEFT);

        ret.setCenter(splitPane);
        ret.setTop(configPane);

        logProducerConfigurationPane.initialize();

        return ret;
    }

    private TextField emphasizedPatternField(){
        final TextField ret = new TextField();
        ret.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> val, String oldValue, String newValue) {
                try {
                    Pattern.compile(newValue);
                    ret.getStyleClass().remove("invalidField");
                } catch (PatternSyntaxException pse) {
                    ret.getStyleClass().add("invalidField");
                }
            }
        });
        ret.textProperty().bindBidirectional(emphasisedEntryText);
        return ret;
    }

    private Button gcButton() {
        return ButtonBuilder.create().text("GC").onAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Runtime.getRuntime().gc();
                }
            }).styleClass("buttonOther").build();
    }

    private Button clearButton() {
        Button clearButton = new Button(Character.toString(TRASH_O));
        clearButton.getStyleClass().add(FONT_AWESOME_BUTTON_STYLE);
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logStore.deleteAll();
            }
        });
        clearButton.setTooltip(new Tooltip("Delete all stored log entries"));
        return clearButton;
    }

    private Button clearLowerThanButton(final Level level, char icon){
        Button clearButton = new Button(new String(new char[]{TRASH_O, '<', icon}));
        clearButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        clearButton.setGraphic(
                HBoxBuilder.create().children(
                        new Label(new String(new char[]{TRASH_O, '<'})),
                        GuiUtils.fillLabeledForLevel(new Label(), level)
                ).build());
        clearButton.getStyleClass().add(FONT_AWESOME_BUTTON_STYLE);
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logStore.deleteLowerThan(level);
            }
        });
        clearButton.setTooltip(new Tooltip("Delete all stored log entries with level lower than " + level));
        return clearButton;
    }

    private ToggleButton expandButton(final Node hiddenConfigPane) {
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
        expandButton.getStyleClass().add(FONT_AWESOME_BUTTON_STYLE);
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
