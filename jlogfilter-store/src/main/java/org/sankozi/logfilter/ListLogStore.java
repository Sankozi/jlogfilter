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
    private final List<Runnable> listeners = Lists.newArrayListWithCapacity(3);
    private final List<LogEntry> entries = Lists.newArrayListWithCapacity(100);

    @Override
    public synchronized void add(LogEntry newEntry) {
        entries.add(newEntry);
        fireNewEntriesListeners();
    }

    @Override
    public synchronized void addAll(Collection<LogEntry> entries) {
        this.entries.addAll(entries);
        fireNewEntriesListeners();
    }

    @Override
    public synchronized void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private synchronized void fireNewEntriesListeners(){
        for(Runnable runnable: listeners){
            runnable.run();
        }
    }

    @Override
    public synchronized void clear() {
        entries.clear();
        fireNewEntriesListeners();
    }

    @Override
    public synchronized List<LogEntry> getTop(int n) {
        if(entries.size() < n) {
            return ImmutableList.copyOf(entries.subList(0, entries.size()));
        } else {
            return ImmutableList.copyOf(entries.subList(entries.size() - n, entries.size()));
        }
    }
}
