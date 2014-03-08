package org.sankozi.jlogfilter.gui;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.beans.property.ListProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import org.sankozi.jlogfilter.LogEntry;

import javax.annotation.Nullable;
import java.util.NavigableSet;

/**
 *
 */
public class DetailPane extends BorderPane {
    private LogEntry logEntry;
    private StringBuilder contentBuilder = new StringBuilder(50);

    private WebView detailArea = new WebView();

    @Inject
    @Named("emphasisedStacktraces")
    ListProperty<String> emphasisedStacktracePackages;

    {
        this.setCenter(detailArea);
    }

    public void setLogEntry(@Nullable LogEntry entry){
        NavigableSet<String> categoryPrefixes = Sets.newTreeSet(emphasisedStacktracePackages.get());
        if(this.logEntry == entry){
            return;
        }
        //System.out.println("setLogEntry " + entry);
        this.logEntry = entry;
        if(this.logEntry != null){
            contentBuilder.setLength(0);
            contentBuilder.append("<!DOCTYPE html>\n"
                                 + "<html lang=\"en\">\n"
                                 + "<style type=\"text/css\">"
                                 + "pre { margin:0px; white-space:pre-wrap; }"
                                 + "pre.emph {font-weight:bold; border:1px solid red; background-color:#FFFFCC;}"
                                 + "</style>"
                                 + "<head>"
                                 + "</head><body>")
                    .append("<pre width='100%'>")
                    .append(logEntry.getMessage())
                    .append('\n') ;
            boolean emph = false;
            for(String stack: logEntry.getStacktrace()){
                boolean nextEmph;
                if(stack.startsWith("\tat ")){
                    String prefix = categoryPrefixes.floor(stack.substring(4));
                    nextEmph = prefix != null && stack.startsWith("\tat " + prefix);
                } else {
                    String prefix = categoryPrefixes.floor(stack);
                    nextEmph = prefix != null && stack.startsWith(prefix);
                }
                if(nextEmph){
                    if(!emph){
                        contentBuilder.append("</pre><pre class='emph'>");
                        emph = true;
                    }
                } else if (emph) {
                    contentBuilder.append("</pre><pre>");
                    emph = false;
                }
                contentBuilder.append(stack).append('\n');
            }
            contentBuilder.append("</pre>")
                          .append("</body></html>");
            detailArea.getEngine().loadContent(contentBuilder.toString());
        } else {
            detailArea.getEngine().loadContent("<html></html>");
        }
    }

}
