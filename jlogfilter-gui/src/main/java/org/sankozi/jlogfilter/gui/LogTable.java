package org.sankozi.jlogfilter.gui;

import com.google.common.base.Strings;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.sankozi.jlogfilter.Level;
import org.sankozi.jlogfilter.LogEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 */
public final class LogTable extends TableView<LogEntry> {

    private volatile boolean refreshing = false;

    private Pattern emphasizedPattern;

    public boolean isRefreshing(){
        return refreshing;
    }

    {
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setRowFactory(new Callback<TableView<LogEntry>, TableRow<LogEntry>>() {
            @Override
            public TableRow<LogEntry> call(TableView<LogEntry> table) {
                return new TableRow<LogEntry>(){
                    @Override
                    protected void updateItem(LogEntry entry, boolean empty) {
                        super.updateItem(entry, empty);
                        if(!empty) {
                            this.getStyleClass().removeAll(Level.LEVEL_NAMES);
                            this.getStyleClass().add(entry.getLevel().name());
                            if(emphasizedPattern != null && emphasizedPattern.matcher(entry.getMessage()).matches()){
                                this.getStyleClass().add("EMPH");
                            } else {
                                this.getStyleClass().remove("EMPH");
                            }
                        } else {
                            this.getStyleClass().removeAll(Level.LEVEL_NAMES);
                            this.getStyleClass().remove("EMPH");
                        }
                    }
                };
            }
        });
    }

    void emphasizePattern(String pattern){
        System.out.println("new pattern : " + pattern);
        try {
            this.emphasizedPattern = Strings.nullToEmpty(pattern).trim().isEmpty() ? null : Pattern.compile(pattern);
        } catch (PatternSyntaxException pse) {
            this.emphasizedPattern = null;
        }
    }

    void scrollTo(String text){

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
