package org.sankozi.jlogfilter.gui;

import com.google.common.collect.Lists;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.sankozi.jlogfilter.LogProducer;
import org.sankozi.jlogfilter.log4j.SocketHubAppenderLogProducer;

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
        getChildren().add(new LogProducerCreateNode());
    }
}

class LogProducerCreateNode extends HBox {
    HBox customParameters = HBoxBuilder.create().alignment(Pos.BASELINE_LEFT).spacing(4).build();

    public LogProducerCreateNode() {
        setAlignment(Pos.BASELINE_LEFT);
        setSpacing(4);

        getChildren().add(new Label("Add new source. Type:"));

        ComboBox<String> sourceType = new ComboBox<>();
        sourceType.getItems().addAll("Log4j SocketHubAppender");
        sourceType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> val, String before, String after) {
                customParameters.getChildren().clear();
                switch(after){
                    case "Log4j SocketHubAppender":
                        customParameters.getChildren().addAll(new Label("host"), new TextField("localhost"),
                                new Label("port"), TextFieldBuilder.create().text("7777").prefWidth(55).build());
                        break;
                }
            }
        });
        getChildren().add(sourceType);
        getChildren().add(customParameters);
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
