package org.sankozi.logfilter.gui;

import com.google.common.collect.Lists;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.List;

/**
 *  Usable version of TilePane, that doesn't change number of columns
 */
public final class SaneTilePane extends GridPane{
    private int columnsSize = 2;

    private int columnIndex = 0;
    private int rowIndex = 0;

    public void addAll(Node... nodes){
        for(Node node : nodes){
            addNode(node);
        }
    }

    public void addNode(Node node){
        this.getChildren().add(node);
        GridPane.setColumnIndex(node, columnIndex);
        GridPane.setRowIndex(node, rowIndex);
        columnIndex++;
        if(columnIndex >= columnsSize){
            columnIndex = 0;
            rowIndex++;
        }
    }

    public int getColumnsSize() {
        return columnsSize;
    }

    /**
     * Sets new number of columns. If value was changed and this pane has children they will be cleared and added again.
     * @param columnsSize
     */
    public void setColumnsSize(int columnsSize) {
        if(this.columnsSize == columnsSize){
            return;
        }
        this.columnsSize = columnsSize;
        this.columnIndex = 0;
        this.rowIndex = 0;
        if(!getChildren().isEmpty()){
            List<Node> children = Lists.newArrayList(getChildren());
            getChildren().clear();
            getChildren().addAll(children);
        }
    }
}
