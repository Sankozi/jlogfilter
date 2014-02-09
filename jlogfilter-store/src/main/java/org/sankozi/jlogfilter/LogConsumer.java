package org.sankozi.jlogfilter;

/**
 * Object that accepts new LogEntries
 */
public interface LogConsumer {
    void add(LogEntry newEntry);
}
