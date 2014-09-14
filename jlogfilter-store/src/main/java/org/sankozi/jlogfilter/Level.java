package org.sankozi.jlogfilter;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 *
 */
public enum Level {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
    ;

    public static final Set<String> LEVEL_NAMES;
    static {
        ImmutableSet.Builder<String> set = ImmutableSet.builder();
        for(Level level: Level.values()){
            set.add(level.name());
        }
        LEVEL_NAMES = set.build();
    }

    public boolean isLower(Level other){
        return ordinal() < other.ordinal();
    }

}
