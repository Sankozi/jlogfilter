package org.sankozi.jlogfilter.gui;

import com.google.inject.Inject;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.WindowEvent;
import org.sankozi.jlogfilter.LogStore;
import org.sankozi.jlogfilter.Statistics;


/**
 *
 */
public class CategoryStatisticsPopup extends Tooltip {

    private TreeCell cell;
    private Node content;
    private Label categoryPrefixLabel;
    private Label categoryStatisticsLabel;
    private Label subcategoriesStatisticsLabel;

    @Inject
    private LogStore logStore;

    {
//        ownerNodeProperty().addListener(new ChangeListener<Node>() {
//            @Override
//            public void changed(ObservableValue<? extends Node> observableValue, Node oldOwner, Node newOwner) {
//                System.out.println("owner changed to " + newOwner);
//            }
//        });
//        ownerWindowProperty().addListener(new ChangeListener<Window>() {
//            @Override
//            public void changed(ObservableValue<? extends Window> observableValue, Window oldWindow, Window newWindow) {
//                System.out.println("window changed to " + newWindow);
//            }
//        });
        contentDisplayProperty().set(ContentDisplay.GRAPHIC_ONLY);
        addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                CategoryTreeItem item = (CategoryTreeItem) cell.getTreeItem();
                if (item != null) {
                    if(content == null){
                        createContent(item);
                        setGraphic(content);
                    } else {
                        updateContent(item);
                    }
//                    setText(item.getCategoryPrefix());
//                    setGraphic();
                }
            }
        });
    }

    private void updateContent(CategoryTreeItem item) {
        Statistics statistics = logStore.getStatistics();
        String categoryPrefix = item.getCategoryPrefix();
        String categoryStatisticDescription = statistics.getCategoryStatisticDescription(categoryPrefix);
        categoryPrefixLabel.setText(categoryPrefix);
        categoryStatisticsLabel.setText(categoryStatisticDescription);
        String subcategoriesStatisticDescription = statistics.getSubcategoriesStatisticDescription(categoryPrefix);
        subcategoriesStatisticsLabel.setText(subcategoriesStatisticDescription);
    }

    private void createContent(CategoryTreeItem item) {
        content = VBoxBuilder.create().children(
                    categoryPrefixLabel = new Label(),
                    new Separator(),
                    new Label("Category stats:"),
                    categoryStatisticsLabel = new Label(""),
                    new Separator(),
                    new Label("Subcategories stats:"),
                    subcategoriesStatisticsLabel = new Label("")
                )
                .styleClass("biggerTooltip")
                .build();
        updateContent(item);
    }

    public TreeCell getCell() {
        return cell;
    }

    public void setCell(TreeCell cell) {
        this.cell = cell;
    }
}
