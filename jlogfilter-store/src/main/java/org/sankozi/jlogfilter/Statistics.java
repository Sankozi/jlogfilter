package org.sankozi.jlogfilter;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 *  Class containing information about registered logging entries
 */
public final class Statistics {
    ConcurrentMap<String, CategoryStatistics> categoryStatistics = Maps.newConcurrentMap();

    long counter = 0;

    void registerEntries(Collection<LogEntry> entries){
        for(LogEntry le: entries){
            CategoryStatistics cat = categoryStatistics.get(le.getCategory());
            if(cat == null){
                cat = new CategoryStatistics();
                categoryStatistics.put(le.getCategory(), cat);
            }
            cat.registerEntry(le);
            counter ++;
            if(counter % 512 == 0){
                System.out.append("Statistics:\n").println(this.toString());
            }
        }
    }

    @Override
    public String toString() {
        return categoryStatistics.toString();
    }
}

final class CategoryStatistics {
    //EnumMap<Level, Long> levelStatistics = new EnumMap<>(Level.class);
    long[] levelStatistics = new long[Level.values().length];

    void registerEntry(LogEntry le){
        levelStatistics[le.getLevel().ordinal()]++;
    }

    @Override
    public String toString(){
        StringBuilder ret = new StringBuilder();
        for(Level level: Level.values()){
            ret.append(level.name()).append(":").append(levelStatistics[level.ordinal()]).append(' ');
        }
        ret.append('\n');
        return ret.toString();
    }
}
