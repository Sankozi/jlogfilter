package org.sankozi.logfilter;

/**
 * Object that accepts new LogEntries
 */
public interface LogConsumer {
    void add(LogEntry newEntry);
}
