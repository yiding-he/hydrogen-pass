package com.hyd.pass.model;

import java.util.ArrayList;
import java.util.List;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class Category extends OrderedItem {

    private long id = System.currentTimeMillis();

    private long parentId;

    private List<Category> children = new ArrayList<>();

    public Category() {
    }

    public Category(String name) {
        setName(name);
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
