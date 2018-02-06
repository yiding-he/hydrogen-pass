package com.hyd.pass.fx;

import com.hyd.fx.components.MenuBuilder;
import com.hyd.pass.dialogs.EditCategoryDialog;
import com.hyd.pass.model.Category;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class CategoryTreeCell extends TreeCell<Category> {

    private ContextMenu contextMenu = MenuBuilder.contextMenu(
            MenuBuilder.menuItem("编辑", this::editItem)
    );

    public CategoryTreeCell() {
        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editItem();
            }
        });
    }

    @Override
    public void updateItem(Category item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
        } else {
            getTreeItem().setGraphic(Icons.Folder.getIconImageView());
            setText(item.getName());
        }
    }

    private void editItem() {
        ButtonType buttonType = new EditCategoryDialog().showAndWait().orElse(ButtonType.CANCEL);
        if (buttonType == ButtonType.OK) {

        }
    }
}
