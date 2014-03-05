package org.sankozi.jlogfilter.gui;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.sankozi.jlogfilter.LogEntry;
import org.sankozi.jlogfilter.LogStore;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public final class LogTable extends TableView<LogEntry> {

    IntegerProperty logEntriesTableSize;

    void refresh(List<LogEntry> entries){
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
    }

    public void setLogEntriesTableSize(IntegerProperty logEntriesTableSize) {
        this.logEntriesTableSize = logEntriesTableSize;
    }
}
