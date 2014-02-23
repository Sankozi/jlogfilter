package org.sankozi.jlogfilter;

import com.google.common.base.Function;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Immutable object containing information about Log entry
 */
public final class LogEntry {
    final static Function<LogEntry, Integer> TO_ID = new Function<LogEntry, Integer>() {
        @Override
        public Integer apply(LogEntry input) {
            return input.getId();
        }
    };

    private final int id;
    private final String category;
    private final String message;
    private final Level level;
    private final String stacktrace;

    LogEntry(int id, Level level, String category, String message, String stacktrace) {
        this.id = id;
        this.level = level;
        this.category = category;
        this.message = message;
        this.stacktrace = stacktrace;
    }

    public int getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public String getStacktrace() {
        return stacktrace;
    }
}
