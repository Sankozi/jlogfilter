package org.sankozi.logfilter;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.sankozi.logfilter.gui.GuiModule;

import java.util.List;

/**
 *
 */
public class App extends com.cathive.fx.guice.GuiceApplication {
    public static final void main(String[] args){
        System.out.println("Test");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(getInjector().getInstance(Key.get(Pane.class, Names.named("main"))), 300, 250));
        primaryStage.show();
    }

    @Override
    public void init(List<Module> modules) throws Exception {
        modules.add(new GuiModule());
    }
}
