package com.hyd.pass.fx;

import com.hyd.fx.cells.TreeViewHelper;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.function.Function;

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
        this.setEditable(true);
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

    /**
     * 遍历所有树节点
     *
     * @param function 树节点 -> 是否继续遍历
     */
    private void iterateTreeItems(Function<TreeItem<Category>, Boolean> function) {
        TreeItem<Category> root = getRoot();
        Boolean _continue = function.apply(root);
        if (_continue) {
            iterateTreeItems(root, function);
        }
    }

    private void iterateTreeItems(TreeItem<Category> parent, Function<TreeItem<Category>, Boolean> function) {
        for (TreeItem<Category> child : parent.getChildren()) {
            Boolean _continue = function.apply(child);
            if (_continue) {
                iterateTreeItems(child, function);
            } else {
                return;
            }
        }
    }

    public void selectCellByEntry(Entry entry) {
        iterateTreeItems(treeItem -> {
            if (treeItem.getValue().containsEntry(entry)) {
                getSelectionModel().select(treeItem);
                return false;
            }
            return true;
        });
    }
}
