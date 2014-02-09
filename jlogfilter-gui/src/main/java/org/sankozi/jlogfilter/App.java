package org.sankozi.jlogfilter;

import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.sankozi.jlogfilter.gui.GuiModule;
import org.sankozi.jlogfilter.log4j.SocketHubAppenderLogProducer;

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
        Font.loadFont(App.class.getResource("/fontawesome-webfont.ttf").toExternalForm(), 12);
        primaryStage.setScene(new Scene(getInjector().getInstance(Key.get(Pane.class, Names.named("main"))), 700, 550));
        primaryStage.setTitle("jlogfilter");
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