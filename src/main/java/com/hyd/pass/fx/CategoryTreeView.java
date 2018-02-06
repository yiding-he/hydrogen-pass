package com.hyd.pass.fx;

import com.hyd.pass.model.Category;
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
        this.setEditable(true);
    }
}
