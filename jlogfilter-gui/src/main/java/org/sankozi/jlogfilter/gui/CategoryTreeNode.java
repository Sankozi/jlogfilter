package org.sankozi.jlogfilter.gui;

import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.HBox;
import org.sankozi.jlogfilter.Level;

/**
 *
 */
public class CategoryTreeNode extends HBox {

    private final Label minimalLevel = LabelBuilder.create().styleClass("fontAwesome").text("").build();
    private final Label partLabel = new Label("");

    {
        this.getChildren().addAll(minimalLevel, partLabel);
    }

    public void updateItem(CategoryTreeItem treeItem){
        if(treeItem == null){
            minimalLevel.setText("");
            partLabel.setText("");
        } else {
            if(treeItem.getMinimalLevel() == null || treeItem.getMinimalLevel() == Level.TRACE){
                minimalLevel.setText("");
            } else {
                GuiUtils.fillLabeledForLevel(minimalLevel, treeItem.getMinimalLevel());
            }
            partLabel.setText(treeItem.getCategoryPart());
        }
    }
}
