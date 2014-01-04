package org.sankozi.logfilter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Immutable object containing information about Log entry
 */
public final class LogEntry {
    private final static AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final int id;
    private final String category;
    private final String message;
    private final Level level;
    private final String stacktrace;

    public LogEntry(Level level, String category, String message) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.level = level;
        this.category = category;
        this.message = message;
        this.stacktrace = "";
    }

    public LogEntry(Level level, String category, String message, String stacktrace) {
        this.id = ID_GENERATOR.incrementAndGet();
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
