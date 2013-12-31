package org.sankozi.logfilter;

/**
 * Immutable object containing information about Log entry
 */
public final class LogEntry {
    private final String category;
    private final String message;
    private final Level level;
    private final String stacktrace;

    public LogEntry(Level level, String category, String message) {
        this.level = level;
        this.category = category;
        this.message = message;
        this.stacktrace = "";
    }

    public LogEntry(Level level, String category, String message, String stacktrace) {
        this.level = level;
        this.category = category;
        this.message = message;
        this.stacktrace = stacktrace;
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
