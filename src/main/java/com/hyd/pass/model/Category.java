package com.hyd.pass.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.hyd.pass.App;
import com.hyd.pass.utils.AESUtils;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class Category extends OrderedItem {

    private long parentId;

    private List<Category> children = new ArrayList<>();

    @JSONField(serialize = false)
    private List<Entry> entries = new ArrayList<>();

    private List<String> entryStrings = new ArrayList<>();

    private String sortBy;

    public Category() {
    }

    public Category(String name) {
        setName(name);
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
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

    public List<String> getEntryStrings() {
        return entryStrings;
    }

    public void setEntryStrings(List<String> entryStrings) {
        this.entryStrings = entryStrings;
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

    public void addEntry(Entry entry) {
        this.entries.add(entry);
    }

    public void removeEntry(Entry entry) {
        this.entries.remove(entry);
    }

    public void readEntries() {
        if (this.entryStrings == null) {
            return;
        }

        this.entries.clear();
        this.entryStrings.forEach(entryString -> {
            String json = AESUtils.decode128(entryString, App.getMasterPassword());
            Entry entry = JSON.parseObject(json, Entry.class);
            this.entries.add(entry);
        });
    }

    public void saveEntries() {
        if (this.entries == null) {
            return;
        }

        this.entryStrings.clear();
        this.entries.forEach(entry -> {
            String json = JSON.toJSONString(entry);
            String enc = AESUtils.encode128(json, App.getMasterPassword());
            this.entryStrings.add(enc);
        });
    }

    public void iterateChildren(Consumer<Category> consumer) {
        iterateChildren(category -> {
            consumer.accept(category);
            return true;
        });
    }

    public void iterateChildren(Function<Category, Boolean> processor) {
        if (processor.apply(this)) {
            iterateChildren(this, processor);
        }
    }

    private void iterateChildren(Category parent, Function<Category, Boolean> processor) {
        for (Category child : parent.getChildren()) {
            if (processor.apply(child)) {
                iterateChildren(child, processor);
            }
        }
    }

    public boolean containsEntry(Entry entry) {
        return entries != null && entries.contains(entry);
    }
}
