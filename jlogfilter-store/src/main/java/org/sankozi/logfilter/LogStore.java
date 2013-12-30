package org.sankozi.logfilter;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface LogStore {
    void add(LogEntry newEntry);
    void addAll(Collection<LogEntry> enties);

    void clear();

    void addChangeListener(Runnable listener);

    /**
     * Return last n events
     * @param n number of events to return
     * @return List containing up to n elements, can be empty, cannot be null
     */
    List<LogEntry> getTop(int n);
}
