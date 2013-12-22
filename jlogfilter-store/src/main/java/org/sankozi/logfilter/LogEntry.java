package org.sankozi.logfilter;

/**
 *
 */
public final class LogEntry {
    private final String category;
    private final String message;

    public LogEntry(String category, String message) {
        this.category = category;
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }
}
