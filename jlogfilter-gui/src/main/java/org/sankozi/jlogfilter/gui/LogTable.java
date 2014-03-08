package org.sankozi.jlogfilter.gui;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.sankozi.jlogfilter.LogEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public final class LogTable extends TableView<LogEntry> {

    private volatile boolean refreshing = false;

    public boolean isRefreshing(){
        return refreshing;
    }

    void refresh(List<LogEntry> entries){
        refreshing = true;
//        System.out.println("refresh!!");
        final ListIterator<LogEntry> iNew = entries.listIterator();

        LogEntry selectedItem = getSelectionModel().getSelectedItem();
        int currentId = selectedItem == null ? -1 : selectedItem.getId();

        Integer newSelectedIndex = null;
        @Nullable Integer deleteFrom = null;
        ObservableList<LogEntry> currentItems = getItems();
        for(ListIterator<LogEntry> li = currentItems.listIterator(); li.hasNext();){
            if(iNew.hasNext()){
                LogEntry current = li.next();
                LogEntry newEntry = iNew.next();
                if(current.getId() != newEntry.getId()){
                    li.set(newEntry);
                    if(newEntry.getId() == currentId){
                        newSelectedIndex = li.previousIndex();
                    }
                }
            } else {
                deleteFrom = li.nextIndex();
                break;
            }
        }
        if(iNew.hasNext()){
            currentItems.addAll(entries.subList(iNew.nextIndex(), entries.size()));
        } else if(deleteFrom != null){
            currentItems.remove(deleteFrom, currentItems.size());
        }
        if(newSelectedIndex != null){
            getSelectionModel().select(newSelectedIndex);
        }
//        System.out.println("refresh end");
        refreshing = false;
    }
}
