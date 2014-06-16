package org.sankozi.jlogfilter.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.application.Platform;
import javafx.beans.property.MapProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.sankozi.jlogfilter.Level;
import org.sankozi.jlogfilter.LogStore;
import org.sankozi.jlogfilter.Statistics;

import javax.inject.Provider;
import java.util.*;

/**
 *
 */
public class CategoryTreeProvider implements Provider<Node> {
    @Inject
    LogStore logStore;

    @Inject @Named("storedMinimalLevel")
    MapProperty<String, Level> storedMinimalLevel;

    @Inject
    Provider<CategoryStatisticsPopup> categoryStatisticsToolip;

    /**
     * List containing all created items for certain levels (0 - {'org', 'com', 'java' etc}, and so on)
     * list is updated only in logStore listener thread
     */
    @SuppressWarnings("unchecked")
    private final ArrayList<NavigableMap<String, CategoryTreeItem>> createdTreeItems
            = Lists.<NavigableMap<String, CategoryTreeItem>>newArrayList(
                    Maps.<String, CategoryTreeItem>newTreeMap(),
                    Maps.<String, CategoryTreeItem>newTreeMap(),
                    Maps.<String, CategoryTreeItem>newTreeMap());

    private TreeItem<String> root;

    private int categoriesCount = 0;

    private void onChange(){
//        System.out.println("onChange");
        Statistics statistics = logStore.getStatistics();
        NavigableSet<String> categories = new TreeSet<>(statistics.getCategories());
        categories.addAll(storedMinimalLevel.keySet());
        for(String category : categories){
            int level = 0;
            int index = 0;
            int newStart = 0;
            String parentName = null;
            String prefix = "";
            while(index >= 0 && newStart < category.length()) {
                index = category.indexOf('.', newStart);
                final String name = index > 0 ? category.substring(newStart, index) : category.substring(newStart);
                prefix += name;
                newStart = index + 1;
                if(level >= createdTreeItems.size()){
                    createdTreeItems.add(Maps.<String, CategoryTreeItem>newTreeMap());
                }
                if (!createdTreeItems.get(level).containsKey(prefix)) {
                    final int levelToAdd = level;
                    final String parentToAdd = parentName;
                    final String key = prefix;
                    Level minimal = storedMinimalLevel.get(prefix);
                    final CategoryTreeItem item = new CategoryTreeItem(name, minimal);
                    item.setExpanded(true);
                    createdTreeItems.get(levelToAdd).put(prefix, item);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            CategoryTreeItem toAdd = createdTreeItems.get(levelToAdd).get(key);
                            (levelToAdd == 0
                                    ? root
                                    : createdTreeItems.get(levelToAdd - 1).get(parentToAdd))
                                        .getChildren().add(toAdd);
                            updateItem(toAdd);
                        }
                    });
                }
                parentName = prefix;
                prefix += ".";
                level++;
            }
        }
        categoriesCount = categories.size();
    }

    public void updateItem(CategoryTreeItem categoryItem){
        String categoryPrefix = categoryItem.getCategoryPrefix();
        Statistics stats = logStore.getStatistics();
        categoryItem.setMinimalLevel(storedMinimalLevel.get(categoryPrefix));
        categoryItem.setValue(categoryItem.getCategoryDescription() + " " + stats.getCategoryStatisticDescription(categoryPrefix));
    }

    @Override
    public Node get() {
        TreeView<String> ret = new TreeView<>();
        root = new TreeItem<>("");
        root.setExpanded(true);
        ret.setRoot(root);
        ret.setShowRoot(false);
        ret.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                TreeItem<String> itemBefore,
                                TreeItem<String> itemAfter) {
                if(itemAfter instanceof CategoryTreeItem){
                    updateItem((CategoryTreeItem) itemAfter);}
            }
        });
        ret.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> stringTreeView) {
//                CategoryCell cell = new CategoryCell();
//                CategoryStatisticsPopup tooltip = categoryStatisticsToolip.get();
//                cell.setTooltip(tooltip);
//                tooltip.setCell(cell);//no other safe way to pass information to tooltip
                return new CategoryCell();
            }
        });

        logStore.addChangeListener(new Runnable() {
            @Override
            public void run() {
                onChange();
            }
        });
        storedMinimalLevel.addListener(new MapChangeListener<String, Level>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Level> change) {
                String key = change.getKey();
//                System.out.println("updating " + key);
                if(key != null){
                    //depth == number of dots in key
                    updateItem(createdTreeItems.get(countChar(key, '.')).get(key));
                }
            }
        });

        return ret;
    }

    private static int countChar(String seq, char character){
        int ret = 0;
        for(int i = 0; i < seq.length(); ++ i){
            if(seq.charAt(i) == character){
                ++ret;
            }
        }
        return ret;
    }

    private class CategoryCell extends TreeCell<String> implements EventHandler<MouseEvent> {
        private ContextMenu menu;
        private CategoryTreeNode node;
        private CategoryStatisticsPopup tooltip;

        {
            this.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
        }

        @Override
        protected void updateItem(String value, boolean empty) {
            super.updateItem(value, empty);
//            System.out.println("update item = " + value);
            if(empty){
                this.setText(null);
                this.setGraphic(null);
                this.setTooltip(null);
            } else {
                if(tooltip == null){
                    tooltip = categoryStatisticsToolip.get();
                    tooltip.setCell(this);
                }
                setTooltip(tooltip);
                if(node == null){
                    node = new CategoryTreeNode();
                }
                node.updateItem((CategoryTreeItem) this.getTreeItem());
                this.setGraphic(node);
            }
//            System.out.println("updating to " + value + " " + this.getTreeItem());
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            if(this.isEmpty()){
                return;
            }
            if(mouseEvent.getButton() == MouseButton.SECONDARY){
                if(menu == null){
                    createMenu();
                }
                setContextMenu(menu);
            }
        }

        private void createMenu(){
            menu = new ContextMenu();
            EventHandler<ActionEvent> menuHandler = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    String levelName = (((MenuItem)actionEvent.getSource()).getText());
                    CategoryTreeItem item = (CategoryTreeItem) getTreeItem();
                    Level level = Level.valueOf(levelName);
                    if(level != Level.TRACE) {
                        storedMinimalLevel.put(item.getCategoryPrefix(), level);
                    } else {
                        storedMinimalLevel.remove(item.getCategoryPrefix());
                    }
//                    System.out.println("put '" + item.getCategoryPrefix() + "' " + levelName);
                }
            };
            for(Level level: Level.values()){
                MenuItem item = new MenuItem(level.name());
                item.setOnAction(menuHandler);
                menu.getItems().add(item);
            }
        }
    }
}
