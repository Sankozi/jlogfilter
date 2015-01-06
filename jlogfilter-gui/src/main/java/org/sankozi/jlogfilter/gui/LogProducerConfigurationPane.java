package org.sankozi.jlogfilter.gui;

import com.google.common.collect.Lists;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.sankozi.jlogfilter.LogProducer;
import org.sankozi.jlogfilter.log4j.SocketHubAppenderLogProducer;
import sun.rmi.runtime.Log;

import javax.inject.Inject;
import java.util.List;

public class LogProducerConfigurationPane extends VBox {

    @Inject
    ListProperty<LogProducer> logProducers;

    public void initialize(){
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
