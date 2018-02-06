package com.hyd.pass.model;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public abstract class OrderedItem {

    private int order;

    private String name;

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
