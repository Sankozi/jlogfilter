package org.sankozi.jlogfilter;

import org.sankozi.jlogfilter.util.StringPool;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Object for creating LogEntries
 */
public final class LogEntryFactory {
    private final static AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final StringPool stringPool;

    private int id = ID_GENERATOR.incrementAndGet();
    private String category = "";
    private String message = "";
    private Level level = Level.TRACE;
    private String stacktrace ="";

    @Inject
    public LogEntryFactory(StringPool stringPool){
        this.stringPool = stringPool;
    }

    public LogEntry create(){
        try {
            return new LogEntry(id, level, category, message, stacktrace);
        } finally {
            id = ID_GENERATOR.incrementAndGet();
        }
    }

    public LogEntryFactory level(Level level){
        this.level = level;
        return this;
    }

    public LogEntryFactory message(String message){
        this.message = message;
        return this;
    }

    public LogEntryFactory category(String category){
        this.category = stringPool.getString(category);
        return this;
    }

    public LogEntryFactory stacktrace(String stacktrace){
        this.stacktrace = stacktrace;
        return this;
    }
}
