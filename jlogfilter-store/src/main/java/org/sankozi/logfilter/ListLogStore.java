package org.sankozi.logfilter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.List;

/**
 * Log that stores new entries in a list
 */
@Singleton
public class ListLogStore implements LogStore, LogConsumer {
    private final List<Runnable> newEntryListeners = Lists.newArrayListWithCapacity(3);
    private final List<LogEntry> entries = Lists.newArrayListWithCapacity(100);

    @Override
    public void add(LogEntry newEntry) {
        entries.add(newEntry);
        fireNewEntriesListeners();
    }

    @Override
    public void addAll(Collection<LogEntry> entries) {
        this.entries.addAll(entries);
        fireNewEntriesListeners();
    }

    @Override
    public void addNewEntriesListener(Runnable listener) {
        newEntryListeners.add(listener);
    }

    private void fireNewEntriesListeners(){
        for(Runnable runnable: newEntryListeners){
            runnable.run();
        }
    }

    @Override
    public List<LogEntry> getTop(int n) {
        if(entries.size() < n) {
            return entries.subList(0, entries.size());
        } else {
            return entries.subList(entries.size() - n, entries.size());
        }
    }
}
