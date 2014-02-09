package org.sankozi.jlogfilter.test;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketHubAppender;
import org.sankozi.jlogfilter.gui.SaneTilePane;


/**
 *
 */
public class TestApp extends Application{
    private ComboBox<String> categoryChoice;
    private ComboBox<String> levelChoice;
    private TextField messageField;
    private Button submitEventButton;
    private CheckBox throwableCheckBox;

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

        messageField = new TextField("test message");

        throwableCheckBox = new CheckBox();

        submitEventButton = new Button("Submit event");
        submitEventButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Level level = Level.toLevel(levelChoice.getValue());

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
                new Label("Category"), categoryChoice,
                new Label("Level"), levelChoice,
                new Label("Message"), messageField,
                new Label("With throwable"), throwableCheckBox,
                submitEventButton);

        Scene scene = new Scene(mainPane, 300, 300);
        stage.setScene(scene);
        stage.show();
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