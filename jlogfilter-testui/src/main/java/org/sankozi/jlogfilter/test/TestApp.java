package org.sankozi.jlogfilter.test;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketHubAppender;
import org.sankozi.jlogfilter.gui.SaneTilePane;
import org.sankozi.jlogfilter.util.NumberField;

/**
 *
 */
public class TestApp extends Application{
    private ComboBox<String> categoryChoice;
    private ComboBox<String> levelChoice;
    private TextField messageField;
    private Button submitEventButton;
    private CheckBox throwableCheckBox;
    private NumberField repeatMilisecondsField;

    private Timeline repeatMessageTimeline;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        configureRootLogger();

        categoryChoice = new ComboBox<>();
        categoryChoice.setEditable(true);
        categoryChoice.getItems().addAll("com.impl", "com.test", "org.impl", "org.test");
        categoryChoice.setValue("com.impl");

        levelChoice = new ComboBox<>();
        levelChoice.getItems().addAll("TRACE","DEBUG","INFO","WARN","ERROR");
        levelChoice.setValue("DEBUG");

        messageField = new TextField("test message");
        repeatMilisecondsField = new NumberField();

        throwableCheckBox = new CheckBox();

        submitEventButton = new Button("Submit event");
        submitEventButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Level level = Level.toLevel(levelChoice.getValue());
                String text = repeatMilisecondsField.textProperty().getValue();
                if(!text.isEmpty()){
                    int miliseconds = Math.max(Integer.valueOf(text), 10);
                    if(repeatMessageTimeline != null){
                        repeatMessageTimeline.stop();
                    }
                    repeatMessageTimeline = TimelineBuilder.create()
                            .cycleCount(Timeline.INDEFINITE)
                            .keyFrames(new KeyFrame(Duration.millis(miliseconds), new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent actionEvent) {
                                    Logger logger = Logger.getLogger(categoryChoice.getValue());
                                    Level level = Level.toLevel(levelChoice.getValue());
                                    logger.log(level, messageField.getText(), new Throwable());
                                }
                            })).build();
                    repeatMessageTimeline.play();
                }

                Logger logger = Logger.getLogger(categoryChoice.getValue());
                if(throwableCheckBox.isSelected()){
                    logger.log(level, messageField.getText(), new Throwable());
                } else {
                    logger.log(level, messageField.getText());
                }
            }
        });

        SaneTilePane mainPane = new SaneTilePane();
        mainPane.setHgap(5);
        mainPane.setVgap(5);
        mainPane.setPadding(new Insets(5,5,5,5));
        mainPane.addAll(
                label("Category"), categoryChoice,
                label("Level"), levelChoice,
                label("Message"), messageField,
                label("With throwable"), throwableCheckBox,
                label("Log every"), HBoxBuilder.create().alignment(Pos.BASELINE_LEFT).children(repeatMilisecondsField, label("ms")).build(),
                submitEventButton);

        Scene scene = new Scene(mainPane, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    private Label label(String label) {
        return new Label(label);
    }

    private void configureRootLogger() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.TRACE);
//        ConsoleAppender consoleAppender = new ConsoleAppender();
//        consoleAppender.setTarget(ConsoleAppender.SYSTEM_OUT);
//        consoleAppender.setName("out");
//        consoleAppender.setLayout(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN));
//        consoleAppender.activateOptions();
//        rootLogger.addAppender(consoleAppender);
        rootLogger.addAppender(new SocketHubAppender(7777));
    }
}
