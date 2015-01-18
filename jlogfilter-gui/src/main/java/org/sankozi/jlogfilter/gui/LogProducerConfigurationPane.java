package org.sankozi.jlogfilter.gui;

import com.google.common.collect.Lists;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.sankozi.jlogfilter.LogProducer;
import org.sankozi.jlogfilter.log4j.SocketHubAppenderLogProducer;

import static org.sankozi.jlogfilter.gui.GuiUtils.*;
import static org.sankozi.jlogfilter.gui.FontAwesomeIcons.*;

import javax.inject.Inject;

public class LogProducerConfigurationPane extends VBox {

    @Inject
    ListProperty<LogProducer> logProducers;

    public void initialize(){
        setSpacing(5);
        setPadding(new Insets(5));
        logProducers.addListener(new ChangeListener<ObservableList<LogProducer>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<LogProducer>> value,
                                ObservableList<LogProducer> before, ObservableList<LogProducer> after) {
                onListChange();
            }
        });
        onListChange();
    }

    public void onListChange(){
        int i = 0;
        if(!getChildren().isEmpty()){
            getChildren().remove(getChildren().size() - 1);
        }
        for (LogProducer logProducer : logProducers) {
            if(i < getChildren().size()){
                Node node = getChildren().get(i);
                if(!((LogProducerEditNode) node).logProducer.equals(logProducer)){
                    getChildren().set(i, LogProducerEditNode.create(logProducer));
                }
            } else {
                getChildren().add(LogProducerEditNode.create(logProducer));
            }
            ++i;
        }
        getChildren().add(new LogProducerCreateNode(logProducers));
    }
}

class LogProducerCreateNode extends HBox {
    public static final String LOG4J_OPTION = "Log4j SocketHubAppender";

    HBox customParameters = HBoxBuilder.create().alignment(Pos.BASELINE_LEFT).spacing(4).build();

    public LogProducerCreateNode(final ListProperty<LogProducer> logProducers) {

        setAlignment(Pos.BASELINE_LEFT);
        setSpacing(4);

        getChildren().add(new Label("Add new source. Type:"));

        final TextField port = TextFieldBuilder.create().text("7777").prefWidth(55).build();
        final TextField host = new TextField("localhost");

        final ComboBox<String> sourceType = new ComboBox<>();
        sourceType.getItems().addAll("Log4j SocketHubAppender");

        sourceType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> val, String before, String after) {
                customParameters.getChildren().clear();
                switch(after){
                    case LOG4J_OPTION:
                        customParameters.getChildren().addAll(new Label("host"), host,  new Label("port"), port);
                        break;
                }
            }
        });
        getChildren().add(sourceType);
        getChildren().add(customParameters);
        getChildren().add(ButtonBuilder.create()
                .prefHeight(sourceType.getHeight())
                .onAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        switch (sourceType.getValue()){
                            case LOG4J_OPTION:
                                logProducers.add(new SocketHubAppenderLogProducer(host.getText(), Integer.valueOf(port.getText())));
                        }
                    }
                })
                .text("Add").build());
    }
}

class LogProducerEditNode extends HBox {
    final LogProducer logProducer;

    public LogProducerEditNode(LogProducer logProducer) {
        this.logProducer = logProducer;
    }

    static LogProducerEditNode create(LogProducer lp){
        LogProducerEditNode ret = new LogProducerEditNode(lp);
        if(lp instanceof SocketHubAppenderLogProducer){
            SocketHubAppenderLogProducer log4jLp = ((SocketHubAppenderLogProducer) lp);
            ret.getChildren().addAll(new Label("Connecting to log4j SocketHubAppender " + log4jLp.getHost() + ":" + log4jLp.getPort()));
        }
        return ret;
    }
}
