package org.sankozi.jlogfilter;

import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import javafx.beans.property.ListProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.sankozi.jlogfilter.gui.Configuration;
import org.sankozi.jlogfilter.gui.ConfigurationStore;
import org.sankozi.jlogfilter.gui.GuiModule;
import org.sankozi.jlogfilter.log4j.SocketHubAppenderLogProducer;

import javax.inject.Inject;
import java.util.List;

/**
 *
 */
public class App extends com.cathive.fx.guice.GuiceApplication {

    public static void main(String[] args){
        launch(args);
    }

    private Services services;

    public static final class Services {
        @Inject ListProperty<LogProducer> logProducers;
        @Inject LogConsumer logConsumer;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(App.class.getResource("/fontawesome-webfont.ttf").toExternalForm(), 12);
        primaryStage.setScene(new Scene(getInjector().getInstance(Key.get(Pane.class, Names.named("main"))), 700, 550));
        primaryStage.setTitle("jlogfilter");
        primaryStage.show();
        services = getInjector().getInstance(Services.class);

        for(LogProducer lp: services.logProducers){
            lp.start(getInjector().getInstance(LogEntryFactory.class), services.logConsumer);
        }
        services.logProducers.addListener(new ListChangeListener<LogProducer>() {
            @Override
            public void onChanged(Change<? extends LogProducer> change) {
                while(change.next()){
                    for (LogProducer logProducer : change.getAddedSubList()) {
                        logProducer.start(getInjector().getInstance(LogEntryFactory.class), services.logConsumer);
                    }
                    for (LogProducer logProducer : change.getRemoved()) {
                        try {
                            logProducer.close();
                        } catch (Exception ex){
                            getInjector().getInstance(LogEntryFactory.class)
                                         .category("jlogfilter")
                                         .level(Level.WARN).message("Error while closing log source " + ex.getMessage())
                                         .stacktrace(ex.getStackTrace())
                                         .create();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void stop() throws Exception {
        for(LogProducer lp: services.logProducers){
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
