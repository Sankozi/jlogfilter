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

/**
 * Log that stores new entries in a list
 */
@Singleton
public class ListLogStore implements LogStore, LogConsumer {
    private final List<Runnable> listeners = Lists.newArrayListWithCapacity(3);
    private final List<LogEntry> entries = Lists.newArrayListWithCapacity(1024);

    private final BlockingQueue<LogEntry> entryQueue = new ArrayBlockingQueue<>(1024);

    private final Statistics statistics = new Statistics();

    private volatile Thread logStoreThread = null;

    private volatile NavigableMap<String, Level> storedMinimalLevel = Maps.newTreeMap();

    @Inject
    public void setStoredMinimalLevel(@Named("storedMinimalLevel") MapProperty<String, Level> storedMinimalLevel){
        System.out.println("storedMinimalLevel " + storedMinimalLevel.get());
        NavigableMap<String, Level> map = Maps.newTreeMap();
        map.putAll(storedMinimalLevel.get());
        this.storedMinimalLevel = map;

        storedMinimalLevel.addListener(new ChangeListener<ObservableMap<String, Level>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableMap<String, Level>> val, ObservableMap<String, Level> map1, ObservableMap<String, Level> map2) {
                System.out.println("updating storedMinimalLevel " + map2);
                NavigableMap<String, Level> map = Maps.newTreeMap();
                map.putAll(map2);
                ListLogStore.this.storedMinimalLevel = map;
            }
        });
    }

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

    private boolean entryStored(LogEntry le){
        Map.Entry<String, Level> entry = storedMinimalLevel.floorEntry(le.getCategory());
        if(entry != null && le.getCategory().startsWith(entry.getKey())){
            return le.getLevel().ordinal() >= entry.getValue().ordinal();
        } else {
            return true;
        }
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
                    if(entryStored(le)){
                        filteredDrain.add(le);
                    }
                }
                //System.out.println("adding " + filteredDrain.size() + " log entries, discarded " + (drain.size() - filteredDrain.size()) + " entries");
                statistics.registerEntries(filteredDrain);
                synchronized (this) {
                    entries.addAll(filteredDrain);
                    fireEntriesChangeListeners();
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
