package com.hyd.pass.fx;

import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import com.hyd.pass.model.SearchItem;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

public class TreeItemBuilder {

    public static TreeItem<SearchItem> buildSearchItemTree(Category rootCategory) {

        SearchItem.CategorySearchItem searchItem = new SearchItem.CategorySearchItem(rootCategory);
        CheckBoxTreeItem<SearchItem> rootTreeItem = new CheckBoxTreeItem<>(searchItem);
        rootTreeItem.setGraphic(Icons.Folder.getIconImageView());
        rootTreeItem.setExpanded(true);

        rootTreeItem.selectedProperty().addListener(
                (ob, oldValue, newValue) -> searchItem.setSelected(newValue));

        rootCategory.getChildren().forEach(
                childCategory -> {
                    TreeItem<SearchItem> childTreeItem = buildSearchItemTree(childCategory);
                    rootTreeItem.getChildren().add(childTreeItem);
                });

        rootCategory.getEntries().forEach(childEntry -> {
            TreeItem<SearchItem> childTreeItem = buildSearchItem(childEntry);
            rootTreeItem.getChildren().add(childTreeItem);
        });

        return rootTreeItem;
    }

    private static TreeItem<SearchItem> buildSearchItem(Entry entry) {
        SearchItem.EntrySearchItem searchItem = new SearchItem.EntrySearchItem(entry);
        CheckBoxTreeItem<SearchItem> treeItem = new CheckBoxTreeItem<>(searchItem);
        treeItem.setGraphic(Icons.Pc.getIconImageView());
        treeItem.setExpanded(true);

        treeItem.selectedProperty().addListener(
                (ob, oldValue, newValue) -> searchItem.setSelected(newValue));

        return treeItem;
    }

}
