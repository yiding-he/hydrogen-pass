package com.hyd.pass.fx;

import com.hyd.pass.dialogs.EditCategoryDialog;
import com.hyd.pass.model.Category;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

import static com.hyd.fx.components.MenuBuilder.contextMenu;
import static com.hyd.fx.components.MenuBuilder.menuItem;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class CategoryTreeCell extends TreeCell<Category> {

    private ContextMenu contextMenu = contextMenu(
            menuItem("编辑", this::editItem),
            menuItem("新建分类...", this::createChild)
    );

    private void createChild() {
        EditCategoryDialog dialog = new EditCategoryDialog("编辑分类", "");
        ButtonType buttonType = dialog.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType == ButtonType.OK) {
            Category category = getItem().createChild(dialog.getCategoryName());
            getTreeItem().getChildren().add(new TreeItem<>(category));
            getTreeItem().setExpanded(true);
        }
    }

    private void editItem() {
        EditCategoryDialog dialog = new EditCategoryDialog("编辑分类", getItem().getName());
        ButtonType buttonType = dialog.showAndWait().orElse(ButtonType.CANCEL);
        if (buttonType == ButtonType.OK) {
            getItem().setName(dialog.getCategoryName());
            getTreeView().refresh();
        }
    }

    public CategoryTreeCell() {
        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    @Override
    public void updateItem(Category item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setGraphic(Icons.Folder.getIconImageView());
            setText(item.getName());
        }
    }
}
