package org.sankozi.jlogfilter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Object that produces log messages and sends them to LogConsumer
 */
@JsonTypeInfo(property = "class", use = JsonTypeInfo.Id.CLASS)
public interface LogProducer extends AutoCloseable, Serializable {
    void start(LogEntryFactory lef, LogConsumer consumer);
}
