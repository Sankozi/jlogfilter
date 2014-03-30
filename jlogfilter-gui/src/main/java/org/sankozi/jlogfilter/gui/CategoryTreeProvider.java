package org.sankozi.jlogfilter.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.sankozi.jlogfilter.LogStore;

import javax.inject.Provider;
import java.util.*;

/**
 *
 */
public class CategoryTreeProvider implements Provider<Node> {
    @Inject
    LogStore logStore;

    /**
     * List containing all created items for certain levels (0 - {'org', 'com', 'java' etc}, and so on)
     * list is updated only in logStore listener thread
     */
    private final ArrayList<NavigableMap<String, TreeItem<String>>> createdTreeItems
            = Lists.<NavigableMap<String, TreeItem<String>>>newArrayList(
                    Maps.<String, TreeItem<String>>newTreeMap(),
                    Maps.<String, TreeItem<String>>newTreeMap(),
                    Maps.<String, TreeItem<String>>newTreeMap());

    private TreeItem<String> root;

    private int categoriesCount = 0;

    private void onChange(){
        NavigableSet<String> categories = new TreeSet<>(logStore.getStatistics().getCategories());
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
                    createdTreeItems.add(Maps.<String, TreeItem<String>>newTreeMap());
                }
                if (!createdTreeItems.get(level).containsKey(prefix)) {
                    final int levelToAdd = level;
                    final String parentToAdd = parentName;
                    final String key = prefix;
                    TreeItem<String> item = new TreeItem<String>(name);
                    item.setExpanded(true);
                    createdTreeItems.get(levelToAdd).put(prefix, item);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            (levelToAdd == 0
                                    ? root
                                    : createdTreeItems.get(levelToAdd - 1).get(parentToAdd))
                                        .getChildren().add(createdTreeItems.get(levelToAdd).get(key));
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

    @Override
    public Node get() {
        TreeView<String> ret = new TreeView<>();
        root = new TreeItem<String>("");
        root.setExpanded(true);
        ret.setRoot(root);
        ret.setShowRoot(false);

        logStore.addChangeListener(new Runnable() {
            @Override
            public void run() {
                onChange();
            }
        });
        return ret;
    }
}
