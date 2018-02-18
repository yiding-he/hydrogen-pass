package com.hyd.pass.model;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public abstract class OrderedItem {

    private long id = System.currentTimeMillis();

    private String name;

    private int order;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
