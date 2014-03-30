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
                    Maps.<String, TreeItem<String>>newTreeMap());

    private TreeItem<String> root;

    private int categoriesCount = 0;

    private void onChange(){
        NavigableSet<String> categories = new TreeSet<>(logStore.getStatistics().getCategories());
        for(String category : categories){
            int level = 0;
            int index = category.indexOf('.');
            final String name = index > 0 ? category.substring(0, index) : category;
            if(!createdTreeItems.get(level).containsKey(name)){
                TreeItem<String> item = new TreeItem<String>(name);
                createdTreeItems.get(level).put(name, item);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        root.getChildren().add(createdTreeItems.get(0).get(name));
                    }
                });
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
        ret.setPrefHeight(100f);

        logStore.addChangeListener(new Runnable() {
            @Override
            public void run() {
                onChange();
            }
        });
        return ret;
    }
}
