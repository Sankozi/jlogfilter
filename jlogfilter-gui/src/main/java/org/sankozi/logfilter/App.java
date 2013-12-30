package org.sankozi.logfilter;

import com.google.common.collect.Lists;
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
import org.sankozi.logfilter.log4j.SocketHubAppenderLogProducer;

import java.util.List;

/**
 *
 */
public class App extends com.cathive.fx.guice.GuiceApplication {

    List<LogProducer> logProducers = Lists.newArrayList();

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(getInjector().getInstance(Key.get(Pane.class, Names.named("main"))), 300, 250));
        primaryStage.show();
        LogConsumer lc = getInjector().getInstance(LogConsumer.class);
        logProducers.add(new SocketHubAppenderLogProducer("localhost",7777));
        for(LogProducer lp: logProducers){
            lp.start(lc);
        }
    }

    @Override
    public void stop() throws Exception {
        for(LogProducer lp: logProducers){
            lp.close();
        }
        super.stop();
    }

    @Override
    public void init(List<Module> modules) throws Exception {
        modules.add(new StoreModule());
        modules.add(new GuiModule());
    }
}
