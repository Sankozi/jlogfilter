package org.sankozi.jlogfilter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Immutable object containing information about Log entry
 */
public final class LogEntry {
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
