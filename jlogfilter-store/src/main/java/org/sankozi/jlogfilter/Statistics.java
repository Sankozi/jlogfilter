package org.sankozi.jlogfilter;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *  Class containing information about registered logging entries
 */
public final class Statistics {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final ConcurrentMap<String, CategoryStatistics> categoryStatistics = Maps.newConcurrentMap();

    long counter = 0;

    void registerEntries(Collection<LogEntry> entries){
        readWriteLock.writeLock().lock();
        try {
            for (LogEntry le : entries) {
                CategoryStatistics cat = categoryStatistics.get(le.getCategory());
                if (cat == null) {
                    cat = new CategoryStatistics();
                    categoryStatistics.put(le.getCategory(), cat);
                }
                cat.registerEntry(le);
                counter++;
                if (counter % 512 == 0) {
                    System.out.append("Statistics:\n").println(this.toString());
                }
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public String getCategoryStatisticDescription(String categoryPrefix){
        readWriteLock.readLock().lock();
        try {
            CategoryStatistics stat = categoryStatistics.get(categoryPrefix);
            if(stat != null){
                return stat.toString();
            } else {
                return "";
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        return categoryStatistics.toString();
    }

    public Set<String> getCategories(){
        readWriteLock.readLock().lock();
        try {
            return Collections.unmodifiableSet(categoryStatistics.keySet());
        } finally {
            readWriteLock.readLock().unlock();
        }
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
            long levelStatistic = levelStatistics[level.ordinal()];
            if(levelStatistic > 0) {
                ret.append(level.name()).append(":").append(levelStatistic).append(' ');
            }
        }
        ret.append('\n');
        return ret.toString();
    }
}
