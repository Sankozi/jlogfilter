package org.sankozi.jlogfilter.gui;

import javafx.scene.control.TreeItem;

/**
 *
 */
public class CategoryTreeItem extends TreeItem<String> {
    private final String categoryPart;
    private String categoryPrefix;

    public CategoryTreeItem(String categoryPart) {
        super(categoryPart);
        this.categoryPart = categoryPart;
    }

    public CategoryTreeItem() {
        this.categoryPart = "";
    }

    public String getCategoryPrefix(){
        if(categoryPrefix == null){
            TreeItem<String> parent = getParent();
            if(parent instanceof CategoryTreeItem){
                categoryPrefix = ((CategoryTreeItem) parent).getCategoryPrefix() + "." + categoryPart;
            } else {
                categoryPrefix = this.getValue();
            }
        }
        return categoryPrefix;
    }

    public String getCategoryPart() {
        return categoryPart;
    }
}
