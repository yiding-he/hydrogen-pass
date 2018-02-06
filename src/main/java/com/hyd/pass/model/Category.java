package com.hyd.pass.model;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class Category extends OrderedItem {

    private long id;

    private long parentId;

    public Category() {
    }

    public Category(String name) {
        setName(name);
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
}
