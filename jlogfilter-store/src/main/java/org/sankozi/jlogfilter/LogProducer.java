package org.sankozi.jlogfilter;

/**
 * Object that
 */
public interface LogProducer extends AutoCloseable{
    void start(LogConsumer consumer);
}
