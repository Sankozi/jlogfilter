package org.sankozi.logfilter.test;

import com.google.common.collect.ImmutableList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.sankozi.logfilter.gui.SaneTilePane;

/**
 *
 */
public class TestApp extends Application{
    private ComboBox<String> categoryChoice;
    private ComboBox<String> levelChoice;

    public static final void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        categoryChoice = new ComboBox<>();
        categoryChoice.setEditable(true);
        categoryChoice.getItems().addAll("com.impl", "com.test", "org.impl", "org.test");

        levelChoice = new ComboBox<>();
        levelChoice.getItems().addAll("TRACE","DEBUG","INFO","WARN","ERROR");

        SaneTilePane mainPane = new SaneTilePane();
        mainPane.setHgap(5);
        mainPane.setVgap(5);
        mainPane.setPadding(new Insets(5,5,5,5));
        mainPane.addAll(
                new Label("Category"), categoryChoice,
                new Label("Level"), levelChoice);

        Scene scene = new Scene(mainPane, 300, 300);
        stage.setScene(scene);
        stage.show();
    }
}
