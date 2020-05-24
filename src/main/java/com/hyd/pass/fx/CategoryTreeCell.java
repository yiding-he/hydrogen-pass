package com.hyd.pass.fx;

import static com.hyd.fx.builders.ImageBuilder.imageView;
import static com.hyd.fx.builders.MenuBuilder.contextMenu;
import static com.hyd.fx.builders.MenuBuilder.menuItem;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.pass.App;
import com.hyd.pass.dialogs.EditCategoryDialog;
import com.hyd.pass.dialogs.SortCategoryChildDialog;
import com.hyd.pass.model.Category;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class CategoryTreeCell extends TreeCell<Category> {

    private final ContextMenu contextMenu = contextMenu(
            menuItem("编辑", imageView("/icons/edit.png"), this::editItem),
            menuItem("新建分类...   ", imageView("/icons/new.png"), this::createChild),
            menuItem("子类排序...   ", imageView("/icons/sort.png"), this::sortChild),
            new SeparatorMenuItem(),
            menuItem("删除", imageView("/icons/delete.png"), this::deleteItem)
    );

    private final ContextMenu rootContextMenu = contextMenu(
            menuItem("编辑", imageView("/icons/edit.png"), this::editItem),
            menuItem("新建分类...   ", imageView("/icons/new.png"), this::createChild),
            menuItem("子类排序...   ", imageView("/icons/sort.png"), this::sortChild)
    );

    private void deleteItem() {
        if (!AlertDialog.confirmYesNo("删除分类",
                "确定要删除“" + getItem().getName() + "”及其下面的所有分类和内容吗？")) {
            return;
        }

        App.getPasswordLib().deleteCategory(getItem());
        ((CategoryTreeView) getTreeView()).deleteTreeItem(getTreeItem());
    }

    private void sortChild() {
        SortCategoryChildDialog dialog = new SortCategoryChildDialog(this.getTreeItem());
        dialog.show();
    }

    private void createChild() {
        EditCategoryDialog dialog = new EditCategoryDialog("编辑分类", "");
        ButtonType buttonType = dialog.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType == ButtonType.OK) {
            Category category = getItem().createChild(dialog.getCategoryName());
            getTreeItem().getChildren().add(new TreeItem<>(category));
            getTreeItem().setExpanded(true);
            App.setPasswordLibChanged();
        }
    }

    private void editItem() {
        EditCategoryDialog dialog = new EditCategoryDialog("编辑分类", getItem().getName());
        ButtonType buttonType = dialog.showAndWait().orElse(ButtonType.CANCEL);
        if (buttonType == ButtonType.OK) {
            getItem().setName(dialog.getCategoryName());
            getTreeView().refresh();
            App.setPasswordLibChanged();
        }
    }

    private ContextMenu getCurrentContextMenu() {
        return isRoot() ? rootContextMenu : contextMenu;
    }

    private boolean isRoot() {
        return getTreeItem() == getTreeView().getRoot();
    }

    public CategoryTreeCell() {
        setOnContextMenuRequested(event -> {
            if (!isEmpty()) {
                getCurrentContextMenu()
                        .show(this, event.getScreenX(), event.getScreenY());
            }
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
