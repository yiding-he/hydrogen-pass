package com.hyd.pass.model;

public abstract class SearchItem {

    private boolean selected;

    public abstract String toString();

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    ///////////////////////////////////////////////

    public static class EntrySearchItem extends SearchItem {

        public Entry entry;

        public EntrySearchItem(Entry entry) {
            this.entry = entry;
        }

        @Override
        public String toString() {
            return entry.getName() + " (" + entry.getLocation() + ")";
        }
    }

    public static class CategorySearchItem extends SearchItem {

        public Category category;

        public CategorySearchItem(Category category) {
            this.category = category;
        }

        @Override
        public String toString() {
            return category.getName();
        }
    }
}
