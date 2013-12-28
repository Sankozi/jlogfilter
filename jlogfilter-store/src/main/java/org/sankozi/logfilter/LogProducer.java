package org.sankozi.logfilter;

/**
 * Object that
 */
public interface LogProducer extends AutoCloseable{
    void start(LogConsumer consumer);
}
