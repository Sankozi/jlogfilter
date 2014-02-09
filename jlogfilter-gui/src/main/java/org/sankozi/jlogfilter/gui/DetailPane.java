package org.sankozi.jlogfilter.gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import org.sankozi.jlogfilter.LogEntry;

import javax.annotation.Nullable;

/**
 *
 */
public class DetailPane extends BorderPane {
    private LogEntry logEntry;
    private StringBuilder contentBuilder = new StringBuilder(50);

    private WebView detailArea = new WebView(); {
//        detailArea.setEditable(false);
    }

    {
        this.setCenter(detailArea);
    }

    public void setLogEntry(@Nullable LogEntry entry){
        this.logEntry = entry;
        if(this.logEntry != null){
            contentBuilder.setLength(0);
            contentBuilder.append("<!DOCTYPE html>\n" +
                                  "<html lang=\"en\">\n" +
                                  "<head></head><body>")
                    .append("<pre width='100%' style='white-space:pre-wrap;'>")
                    .append(logEntry.getMessage())
                    .append('\n')
                    .append(logEntry.getStacktrace())
                    .append("</pre>")
                    .append("</body></html>");
            detailArea.getEngine().loadContent(contentBuilder.toString());
        } else {
            detailArea.getEngine().loadContent("<html></html>");
        }
    }

}
