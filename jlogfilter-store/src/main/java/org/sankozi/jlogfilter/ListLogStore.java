package org.sankozi.jlogfilter;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Log that stores new entries in a list
 */
@Singleton
public class ListLogStore implements LogStore, LogConsumer {
    private final List<Runnable> listeners = Lists.newArrayListWithCapacity(3);
    private final List<LogEntry> entries = Lists.newArrayListWithCapacity(1024);

    private final BlockingQueue<LogEntry> entryQueue = new ArrayBlockingQueue<>(1024);

    private volatile Thread logStoreThread = null;

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public void add(LogEntry newEntry) {
        if(logStoreThread == null){
            synchronized(this){
                if(logStoreThread == null){
                    logStoreThread = initLogStoreThread();
                }
            }
        }
        entryQueue.add(newEntry);
    }

    @Override
    public synchronized void deleteIds(Collection<Integer> ids) {
        Set<Integer> idsToDelete = Sets.newHashSet(ids);
        Iterator<LogEntry> i = entries.iterator();
        while(!idsToDelete.isEmpty()){
            LogEntry le = i.next();
            if(idsToDelete.contains(le.getId())){
                i.remove();
                idsToDelete.remove(le.getId());
            }
        }
        fireEntriesChangeListeners();
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
        try {
            while(true){
                LogEntry entry = entryQueue.take();
                drain.add(entry);
                Thread.sleep(100);
                entryQueue.drainTo(drain);
                System.out.println("adding " + drain.size() + " log entries");
                synchronized (this) {
                    entries.addAll(drain);
                    fireEntriesChangeListeners();
                }
                drain.clear();
            }
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public synchronized void addAll(Collection<LogEntry> entries) {
        this.entries.addAll(entries);
        fireEntriesChangeListeners();
    }

    @Override
    public synchronized void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    private synchronized void fireEntriesChangeListeners(){
        for(Runnable runnable: listeners){
            runnable.run();
        }
    }

    @Override
    public synchronized void deleteAll() {
        entries.clear();
        fireEntriesChangeListeners();
    }

    @Override
    public synchronized List<LogEntry> getTop(int n) {
        if(entries.size() < n) {
            return ImmutableList.copyOf(entries.subList(0, entries.size()));
        } else {
            return ImmutableList.copyOf(entries.subList(entries.size() - n, entries.size()));
        }
    }
}
