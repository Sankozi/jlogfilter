package org.sankozi.logfilter.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.sankozi.logfilter.LogEntry;

import javax.annotation.Nullable;

/**
 *
 */
public class DetailPane extends VBox {
    private LogEntry logEntry;

    private Label messageLabel = new Label();
    private Label stacktraceLabel = new Label();

    {
        this.getChildren().addAll(messageLabel, stacktraceLabel);
    }

    public void setLogEntry(@Nullable LogEntry entry){
        this.logEntry = entry;
        if(this.logEntry != null){
            messageLabel.setText(logEntry.getMessage());
            stacktraceLabel.setText(logEntry.getStacktrace());
        } else {
            messageLabel.setText(null);
            stacktraceLabel.setText(null);
        }
    }
}
