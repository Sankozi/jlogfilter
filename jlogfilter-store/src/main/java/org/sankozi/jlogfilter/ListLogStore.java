package org.sankozi.jlogfilter;

import com.google.common.collect.*;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.beans.property.MapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Log that stores new entries in a list
 */
@Singleton
public class ListLogStore implements LogStore, LogConsumer {
    private final List<Runnable> listeners = Lists.newArrayListWithCapacity(3);
    private final List<LogEntry> entries = Lists.newArrayListWithCapacity(1024);

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final BlockingQueue<LogEntry> entryQueue = new ArrayBlockingQueue<>(1024);

    private final Statistics statistics = new Statistics();

    private volatile Thread logStoreThread = null;

    private @Inject LogEntryFilter logEntryFilter;

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public void addAll(Collection<LogEntry> entries) {
        checkInitThread();
        this.entryQueue.addAll(entries);
    }

    @Override
    public void add(LogEntry newEntry) {
        checkInitThread();
        entryQueue.add(newEntry);
    }

    private void checkInitThread() {
        if(logStoreThread == null){
            readWriteLock.readLock().lock();
            try {
                if(logStoreThread == null){
                    logStoreThread = initLogStoreThread();
                }
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
    }

    @Override
    public void deleteIds(Collection<Integer> ids) {
        readWriteLock.writeLock().lock();
        try {
            Set<Integer> idsToDelete = Sets.newHashSet(ids);
            Iterator<LogEntry> i = entries.iterator();
            while (!idsToDelete.isEmpty() && i.hasNext()) {
                LogEntry le = i.next();
                if (idsToDelete.contains(le.getId())) {
                    i.remove();
                    idsToDelete.remove(le.getId());
                }
            }
            fireEntriesChangeListeners();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void delete(Collection<LogEntry> le) {
        deleteIds(Collections2.transform(le, LogEntry.TO_ID));
    }

    private Thread initLogStoreThread(){
        Thread ret = new Thread(new Runnable() {
            @Override
            public void run() {
                logStoreThread();
            }
        });
        ret.setName("logStoreThread");
        ret.setDaemon(true);
        ret.start();
        return ret;
    }

    private void logStoreThread() {
        List<LogEntry> drain = Lists.newArrayListWithCapacity(32);
        List<LogEntry> filteredDrain = Lists.newArrayListWithCapacity(32);
        try {
            while(true){
                LogEntry entry = entryQueue.take();//blocking
                drain.add(entry);
                Thread.sleep(100);
                entryQueue.drainTo(drain);
                for(LogEntry le : drain){
                    if(logEntryFilter.apply(le)){
                        filteredDrain.add(le);
                    }
                }
                //System.out.println("adding " + filteredDrain.size() + " log entries, discarded " + (drain.size() - filteredDrain.size()) + " entries");
                statistics.registerEntries(filteredDrain);
                readWriteLock.writeLock().lock();
                try {
                    entries.addAll(filteredDrain);
                    fireEntriesChangeListeners();
                } finally {
                    readWriteLock.writeLock().unlock();
                }
                drain.clear();
                filteredDrain.clear();
            }
        } catch (InterruptedException ex) {
            System.out.print("closing log store");
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public synchronized void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private void fireEntriesChangeListeners(){
        for (Runnable runnable : listeners) {
            runnable.run();
        }
    }

    @Override
    public void deleteAll() {
        readWriteLock.writeLock().lock();
        try {
            entries.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
        fireEntriesChangeListeners();
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public List<LogEntry> getTop(int n) {
        readWriteLock.readLock().lock();
        try {
            if (entries.size() < n) {
                return ImmutableList.copyOf(entries.subList(0, entries.size()));
            } else {
                return ImmutableList.copyOf(entries.subList(entries.size() - n, entries.size()));
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
