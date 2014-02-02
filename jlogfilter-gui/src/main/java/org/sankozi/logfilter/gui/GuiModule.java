package org.sankozi.logfilter.gui;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
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

        //properties
        bind(IntegerProperty.class).annotatedWith(Names.named("storedEntriesSize")).toInstance(new SimpleIntegerProperty(0));
        bind(LongProperty.class).annotatedWith(Names.named("totalMemoryKiB")).toInstance(new SimpleLongProperty(0));
        bind(LongProperty.class).annotatedWith(Names.named("freeMemoryKiB")).toInstance(new SimpleLongProperty(0));
    }
}
