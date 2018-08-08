package com.hyd.pass.fx;

import com.hyd.fx.helpers.TreeViewHelper;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class CategoryTreeView extends TreeView<Category> {

    public CategoryTreeView() {
        init();
    }

    public CategoryTreeView(TreeItem<Category> root) {
        super(root);
        init();
    }

    private void init() {
        this.setCellFactory(tv -> new CategoryTreeCell());
    }

    public void deleteTreeItem(TreeItem<Category> treeItem) {
        TreeViewHelper.iterate(this, item -> {
            if (item.getChildren().contains(treeItem)) {
                item.getChildren().remove(treeItem);
                return false;
            } else {
                return true;
            }
        });
    }

    public void selectCellByEntry(Entry entry) {
        TreeViewHelper.iterate(getRoot(), treeItem -> {
            if (treeItem.getValue().containsEntry(entry)) {
                getSelectionModel().select(treeItem);
                return false;
            }
            return true;
        });
    }
}
