package org.sankozi.jlogfilter.gui;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import org.sankozi.jlogfilter.Level;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class GuiModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(Pane.class).annotatedWith(Names.named("main")).toProvider(MainPaneProvider.class);
        bind(LogTable.class).toProvider(LogTableProvider.class);
        bind(Node.class).annotatedWith(Names.named("categoryTree")).toProvider(CategoryTreeProvider.class);

        bind(Path.class).annotatedWith(Names.named("configurationPath")).toInstance(Paths.get(System.getProperty("user.home"), ".jlogfilter", "configuration.json"));

        //properties
        bind(IntegerProperty.class).annotatedWith(Names.named("storedEntriesSize")).toInstance(new SimpleIntegerProperty(0));
        bind(LongProperty.class).annotatedWith(Names.named("totalMemoryKiB")).toInstance(new SimpleLongProperty(0));
        bind(LongProperty.class).annotatedWith(Names.named("freeMemoryKiB")).toInstance(new SimpleLongProperty(0));
        bind(IntegerProperty.class).annotatedWith(Names.named("logEntriesTableSize")).toInstance(new SimpleIntegerProperty(500));
        bind(StringProperty.class).annotatedWith(Names.named("emphasisedEntryText")).toInstance(new SimpleStringProperty(""));

        bind(new TypeLiteral<ListProperty<String>>(){}).annotatedWith(Names.named("emphasisedStacktraces")).toInstance(new SimpleListProperty<String>());
        bind(new TypeLiteral<MapProperty<String, Level>>(){}).annotatedWith(Names.named("storedMinimalLevel")).toInstance(new SimpleMapProperty<String, Level>());
        bind(ConfigurationStore.class).asEagerSingleton();
    }
}
