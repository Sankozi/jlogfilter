package org.sankozi.jlogfilter;

import java.io.Serializable;

/**
 * Object that
 */
public interface LogProducer extends AutoCloseable, Serializable {
    void start(LogEntryFactory lef, LogConsumer consumer);
}
