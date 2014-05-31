package org.sankozi.jlogfilter;


import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.beans.property.MapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;

import javax.inject.Inject;
import java.util.Map;
import java.util.NavigableMap;

/**
 *  Predicate that returns false only for entries of selected category below certain level
 */
@Singleton
public class LogEntryFilter implements Predicate<LogEntry> {

    private volatile NavigableMap<String, Level> storedMinimalLevel = Maps.newTreeMap();

    @Inject
    public void setStoredMinimalLevel(@Named("storedMinimalLevel") MapProperty<String, Level> storedMinimalLevel){
        System.out.println("storedMinimalLevel " + storedMinimalLevel.get());
        NavigableMap<String, Level> map = Maps.newTreeMap();
        map.putAll(storedMinimalLevel.get());
        this.storedMinimalLevel = map;

        storedMinimalLevel.addListener(new ChangeListener<ObservableMap<String, Level>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableMap<String, Level>> val, ObservableMap<String, Level> map1, ObservableMap<String, Level> map2) {
                System.out.println("updating storedMinimalLevel " + map2);
                LogEntryFilter.this.storedMinimalLevel = ImmutableSortedMap.copyOf(map2);
            }
        });
    }

    @Override
    public boolean apply(LogEntry logEntry) {
        return entryStored(logEntry);
    }

    private boolean entryStored(LogEntry le){
        Map.Entry<String, Level> entry = getRuleForEntry(le);
        return entry == null || le.getLevel().ordinal() >= entry.getValue().ordinal();
    }

    public Map.Entry<String, Level> getRuleForEntry(LogEntry le) {
        Map.Entry<String, Level> entry = storedMinimalLevel.floorEntry(le.getCategory());
        if(entry != null && le.getCategory().startsWith(entry.getKey())){//valid entry
            return entry;
        } else {
            return null;
        }
    }
}
