package org.sankozi.logfilter.gui;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import org.sankozi.logfilter.LogEntry;

/**
 *
 */
public class GuiModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(Pane.class).annotatedWith(Names.named("main")).toProvider(MainPaneProvider.class);
        bind(new TypeLiteral<TableView<LogEntry>>(){}).toProvider(LogTableProvider.class);

    }
}
