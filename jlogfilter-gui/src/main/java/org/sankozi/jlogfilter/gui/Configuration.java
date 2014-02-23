package org.sankozi.jlogfilter.gui;

import org.sankozi.jlogfilter.Level;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Configuration {
    DetailPaneLocation detailPaneLocation = DetailPaneLocation.BOTTOM;
    int logEntriesTableSize = 500;
    /** class prefix to minimal LogEntryLevel (entries with lower level are not stored) */
    Map<String, Level> storedMinimalLevel;
    /** category prefix of emphasised stacktrace elements */
    List<String> emphasisedStacktraces = Collections.emptyList();
}


enum DetailPaneLocation {
    NONE,
    LEFT,
    RIGHT,
    BOTTOM
}
