package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.cells.ListCells;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.App;
import com.hyd.pass.model.Category;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;

import java.util.List;
import java.util.function.BiFunction;

/**
 * (description)
 * created at 2018/2/11
 *
 * @author yidin
 */
public class SortCategoryChildDialog extends BasicDialog {

    private TreeItem<Category> treeItem;

    private Category parent;

    @FXML
    public ListView<Category> lvCategories;

    public SortCategoryChildDialog(TreeItem<Category> treeItem) {
        this.treeItem = treeItem;
        this.parent = treeItem.getValue();

        new DialogBuilder()
                .title("子类排序")
                .logo(AppLogo.getLogo())
                .body("/fxml/sort-category-child.fxml", this)
                .buttons(ButtonType.CLOSE)
                .applyTo(this);

        this.lvCategories.setCellFactory(ListCells.cellFactory(Category::getName));
    }

    public void initialize() {
        this.lvCategories.getItems().addAll(this.parent.getChildren());
    }

    public void move(
            BiFunction<List<Category>, Integer, Boolean> canMove,
            BiFunction<List<Category>, Integer, Integer> insertPosition
    ) {
        int selectedIndex = this.lvCategories.getSelectionModel().getSelectedIndex();
        List<Category> items = this.lvCategories.getItems();

        if (canMove.apply(items, selectedIndex)) {
            Category selectedItem = items.remove(selectedIndex);
            items.add(insertPosition.apply(items, selectedIndex), selectedItem);
            this.parent.updateChildrenOrder(items);
            this.parent.applyChildrenOrder(treeItem);
            this.lvCategories.getSelectionModel().select(selectedItem);
            App.setPasswordLibChanged();
        }
    }

    public void moveUp() {
        move(
                (list, selectedIndex) -> selectedIndex > 0,
                (list, selectedIndex) -> selectedIndex - 1
        );
    }

    public void moveDown() {
        move(
                (list, selectedIndex) -> selectedIndex < list.size() - 1,
                (list, selectedIndex) -> selectedIndex + 1
        );
    }

    public void moveTop() {
        move(
                (list, selectedIndex) -> selectedIndex > 0,
                (list, selectedIndex) -> 0
        );
    }

    public void moveBottom() {
        move(
                (list, selectedIndex) -> selectedIndex < list.size() - 1,
                (list, selectedIndex) -> list.size()
        );
    }
}
