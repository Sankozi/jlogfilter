package org.sankozi.jlogfilter.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.sankozi.jlogfilter.Level;
import org.sankozi.jlogfilter.LogProducer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Configuration {
    DetailPaneLocation detailPaneLocation = DetailPaneLocation.BOTTOM;
    int logEntriesTableSize = 500;

    String emphasisedEntryText = null;
    /** class prefix to minimal LogEntryLevel (entries with lower level are not stored) */
    Map<String, Level> storedMinimalLevel = Maps.newHashMap();
    /** category prefix of emphasised stacktrace elements */
    List<String> emphasisedStacktraces = Collections.emptyList();

}

enum LogProducerType {
    LOG4J_SOCKET_HUB("org.sankozi.jlogfilter.log4j.SocketHubAppenderLogProducer");

    private final String clazz;

    LogProducerType(String clazz) {
        this.clazz = clazz;
    }

    public String getClazz() {
        return clazz;
    }
}

enum DetailPaneLocation {
    NONE,
    LEFT,
    RIGHT,
    BOTTOM
}
