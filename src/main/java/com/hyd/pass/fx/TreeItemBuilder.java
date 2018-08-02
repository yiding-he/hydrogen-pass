package com.hyd.pass.fx;

import com.hyd.pass.model.Category;
import com.hyd.pass.model.SearchItem;
import javafx.scene.control.TreeItem;

public class TreeItemBuilder {

    public static TreeItem<SearchItem> buildSearchItemTree(Category root) {
        return new TreeItem<>(new SearchItem.CategorySearchItem(root));
    }

}
