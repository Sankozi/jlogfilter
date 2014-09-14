package org.sankozi.jlogfilter;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface LogStore {
    void add(LogEntry newEntry);
    void addAll(Collection<LogEntry> enties);

    void deleteAll();

    /**
     * Deletes all entries with level equal or lower than selected
     * @param level
     */
    void deleteLowerThan(Level level);

    void deleteIds(Collection<Integer> ids);
    void delete(Collection<LogEntry> le);

    void addChangeListener(Runnable listener);

    /**
     * Return last n events
     * @param n number of events to return
     * @return List containing up to n elements, can be empty, cannot be null
     */
    List<LogEntry> getTop(int n);

    /**
     * Return number of stored events
     * @return number of stored events
     */
    int size();

    Statistics getStatistics();
}
