package org.sankozi.jlogfilter.gui;

import com.google.common.base.Objects;
import javafx.scene.control.TreeItem;
import org.sankozi.jlogfilter.Level;

import javax.annotation.Nullable;

/**
 *
 */
public class CategoryTreeItem extends TreeItem<String> {
    private final String categoryPart;
    private String categoryPrefix;
    private @Nullable Level minimalLevel;

    public CategoryTreeItem(String categoryPart, @Nullable Level minimalLevel) {
        super(categoryPart + (minimalLevel == null ? "" : "(min = " + minimalLevel + ")"));
        this.categoryPart = categoryPart;
        this.minimalLevel = minimalLevel;
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
                categoryPrefix = this.getCategoryPart();
            }
        }
        return categoryPrefix;
    }

    @Nullable
    public Level getMinimalLevel() {
        return minimalLevel;
    }

    public void setMinimalLevel(@Nullable Level minimalLevel) {
        this.minimalLevel = minimalLevel;
    }

    public String getCategoryPart() {
        return categoryPart;
    }

    public String getCategoryDescription(){
        return categoryPart + (minimalLevel == null ? "" : " (min = " + minimalLevel + ")");
    }
}
