package org.sankozi.logfilter.gui;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.sankozi.logfilter.Level;
import org.sankozi.logfilter.LogEntry;
import org.sankozi.logfilter.LogStore;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public class LogTableProvider implements Provider<TableView<LogEntry>> {
    private final static SimpleStringProperty EMPTY_STRING_PROPERTY = new SimpleStringProperty("");

    @Inject @Named("storedEntriesSize")
    IntegerProperty storedEntriesSize;

    @Inject
    LogStore logStore;

    private final TableColumn<LogEntry, String> messageColumn = new TableColumn<>("Message"); {
        messageColumn.setSortable(false);
        messageColumn.setMinWidth(200);
        messageColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LogEntry, String> cell) {
                String message = cell.getValue().getMessage();
                if(message.isEmpty()){
                    return EMPTY_STRING_PROPERTY;
                } else {
                    int newline = message.indexOf('\n');
                    if(newline != -1) {
                        return new SimpleStringProperty(message.substring(0, newline) + "[...]");
                    } else {
                        return new SimpleStringProperty(message);
                    }
                }
            }
        });
    }

    private final TableColumn<LogEntry, String> categoryColumn = new TableColumn<LogEntry, String>("Category"); {
        categoryColumn.setSortable(false);
        categoryColumn.setMinWidth(200);
        categoryColumn.setCellValueFactory(new PropertyValueFactory<LogEntry, String>("category"));
    }

    private final TableColumn<LogEntry, String> levelColumn = new TableColumn<LogEntry, String>("Level"); {
        levelColumn.setSortable(false);
        levelColumn.setMinWidth(50);
        levelColumn.setMaxWidth(50);
        levelColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LogEntry, String> cell) {
                return new SimpleStringProperty(cell.getValue().getLevel().name());
            }
        });
    }

    private final TableColumn<LogEntry, String> stacktraceColumn = new TableColumn<LogEntry, String>("Stacktrace"); {
        stacktraceColumn.setSortable(false);
        stacktraceColumn.setMinWidth(100);
        stacktraceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LogEntry, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LogEntry, String> cell) {
                String stacktrace = cell.getValue().getStacktrace();
                if(stacktrace.isEmpty()){
                    return EMPTY_STRING_PROPERTY;
                } else {
                    return new SimpleStringProperty(stacktrace.substring(0, stacktrace.indexOf('\n')) + "[...]");
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public TableView<LogEntry> get() {
        final TableView<LogEntry> ret = new TableView<LogEntry>();
        ret.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ret.getColumns().addAll(levelColumn, messageColumn, categoryColumn, stacktraceColumn);
        final Callback<TableView<LogEntry>, TableRow<LogEntry>> rowFactory = ret.getRowFactory();
        ret.setRowFactory(new Callback<TableView<LogEntry>, TableRow<LogEntry>>() {
            @Override
            public TableRow<LogEntry> call(TableView<LogEntry> table) {
                return new TableRow<LogEntry>(){
                    @Override
                    protected void updateItem(LogEntry entry, boolean empty) {
                        super.updateItem(entry, empty);
                        if(!empty) {
                            this.getStyleClass().removeAll(Level.LEVEL_NAMES);
                            this.getStyleClass().add(entry.getLevel().name());
                        }
                    }
                };
            }
        });
        ret.setMaxHeight(ret.getMinHeight());
        logStore.addChangeListener(new Runnable() {
            @Override
            public void run() {
                final List<LogEntry> entries = logStore.getTop(500);
                final ListIterator<LogEntry> iNew = entries.listIterator();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        storedEntriesSize.set(logStore.size());
                        LogEntry selectedItem = ret.getSelectionModel().getSelectedItem();
                        int currentId = selectedItem == null ? -1 : selectedItem.getId();

                        Integer newSelectedIndex = null;
                        @Nullable Integer deleteFrom = null;
                        ObservableList<LogEntry> currentItems = ret.getItems();
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
                            ret.getSelectionModel().select(newSelectedIndex);
                        }
                    }
                });
            }
        });
        return ret;
    }
}
