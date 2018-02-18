package com.hyd.pass.model;

import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class Category extends OrderedItem {

    private long parentId;

    private List<Category> children = new ArrayList<>();

    private List<Entry> entries = new ArrayList<>();

    public Category() {
    }

    public Category(String name) {
        setName(name);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public Category createChild(String categoryName) {
        Category category = new Category(categoryName);
        category.setParentId(getId());
        category.setOrder(children.size());
        children.add(category);
        return category;
    }

    public void updateChildrenOrder(List<Category> orderedChildren) {
        for (int i = 0; i < orderedChildren.size(); i++) {
            Category child = orderedChildren.get(i);
            child.setOrder(i);
        }

        this.children.sort(Comparator.comparing(Category::getOrder));
    }

    public void applyChildrenOrder(TreeItem<Category> thisTreeItem) {
        thisTreeItem.getChildren().sort(
                Comparator.comparing(treeItem -> treeItem.getValue().getOrder()));
    }
}
