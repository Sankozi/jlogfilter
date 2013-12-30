package org.sankozi.logfilter;


import com.google.common.base.Predicate;

/**
 *  Predicate that returns false only for entries of selected category below certain level
 */
public class LogEntryFilter implements Predicate<LogEntry> {
    public final String categoryPrefix;
    public final Level minimalLevel;

    public LogEntryFilter(String category, Level minimalLevel) {
        this.categoryPrefix = category;
        this.minimalLevel = minimalLevel;
    }

    @Override
    public boolean apply(LogEntry logEntry) {
        return !logEntry.getCategory().startsWith(categoryPrefix)
                || logEntry.getLevel().ordinal() >= minimalLevel.ordinal();
    }
}
