package org.sankozi.jlogfilter.gui;

import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
                switch (treeItem.getMinimalLevel()){
                    case DEBUG:
                        minimalLevel.setText(FontAwesomeIcons.BUG + "");
                        minimalLevel.setTextFill(Color.DARKGRAY);
                        break;
                    case INFO:
                        minimalLevel.setText(FontAwesomeIcons.INFO + "");
                        minimalLevel.setTextFill(Color.DARKGREEN);
                        break;
                    case WARN:
                        minimalLevel.setText(FontAwesomeIcons.WARNING + "");
                        minimalLevel.setTextFill(Color.DARKORANGE);
                        break;
                    case ERROR:
                        minimalLevel.setText(FontAwesomeIcons.ERROR + "");
                        minimalLevel.setTextFill(Color.DARKRED);
                        break;
                    case FATAL:
                        minimalLevel.setText(FontAwesomeIcons.FIRE + "");
                        minimalLevel.setTextFill(Color.DARKRED);
                }
            }
            partLabel.setText(treeItem.getCategoryPart());
        }
    }
}
