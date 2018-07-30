package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.cells.TreeCellFactoryBuilder;
import com.hyd.fx.components.FilterableTreeView;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.App;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import org.apache.commons.lang3.StringUtils;

import static com.hyd.fx.builders.ImageBuilder.image;

/**
 * @author yidin
 */
public class SearchDialog extends BasicDialog {

    @FXML
    private TextField txtKeyword;

    @FXML
    private FilterableTreeView<SearchItem> tvSearchResult;

    @FXML
    private Button btnClear;

    private Entry selectedEntry;

    public SearchDialog() {
        new DialogBuilder()
                .body("/fxml/search.fxml", this)
                .owner(AppPrimaryStage.getPrimaryStage())
                .title("关键字搜索")
                .buttons(ButtonType.CLOSE)
                .onButtonClicked(ButtonType.CLOSE, this::closeButtonClicked)
                .onStageShown(this::onStageShown)
                .applyTo(this);
    }

    public Entry getSelectedEntry() {
        return selectedEntry;
    }

    private void onStageShown(DialogEvent dialogEvent) {
        this.txtKeyword.requestFocus();
    }

    public void initialize() {
        this.btnClear.setOnAction(event -> this.clear());
        this.txtKeyword.textProperty().addListener((ob, oldValue, newValue) -> this.keywordChanged(newValue));
        this.tvSearchResult.setOriginalRoot(buildOriginalRoot());

        new TreeCellFactoryBuilder<SearchItem>()
                .setToString(SearchItem::toString)
                .setOnDoubleClick(this::searchItemSelected)
                .setIconSupplier(this::getTreeNodeIcon)
                .setTo(tvSearchResult);
    }

    private Image getTreeNodeIcon(TreeItem<SearchItem> treeItem) {
        if (treeItem.getValue() instanceof CategorySearchItem) {
            return image("/icons/folder.png");
        } else if (treeItem.getValue() instanceof EntrySearchItem) {
            return image("/icons/pc.png");
        } else {
            return null;
        }
    }

    private TreeItem<SearchItem> buildOriginalRoot() {
        Category rootCategory = App.getPasswordLib().getRootCategory();
        TreeItem<SearchItem> rootTreeItem = new TreeItem<>(new CategorySearchItem(rootCategory));

        buildOriginalRoot(rootCategory, rootTreeItem);
        return rootTreeItem;
    }

    private void buildOriginalRoot(Category parentCategory, TreeItem<SearchItem> parentTreeItem) {

        parentTreeItem.setExpanded(true);

        for (Category child : parentCategory.getChildren()) {
            TreeItem<SearchItem> childTreeItem = new TreeItem<>(new CategorySearchItem(child));
            parentTreeItem.getChildren().add(childTreeItem);
            buildOriginalRoot(child, childTreeItem);
        }

        for (Entry entry : parentCategory.getEntries()) {
            TreeItem<SearchItem> childTreeItem = new TreeItem<>(new EntrySearchItem(entry));
            parentTreeItem.getChildren().add(childTreeItem);
        }
    }

    private void searchItemSelected(SearchItem item) {
        if (item instanceof EntrySearchItem) {
            entrySelected(((EntrySearchItem) item).entry);
        }
    }

    private void entrySelected(Entry entry) {
        this.selectedEntry = entry;
        this.close();
    }

    private void keywordChanged(String keyword) {
        this.tvSearchResult.filter(searchItem -> {

            if (searchItem instanceof CategorySearchItem) {
                return ((CategorySearchItem) searchItem).category.getName().contains(keyword);

            } else if (searchItem instanceof EntrySearchItem) {
                Entry entry = ((EntrySearchItem) searchItem).entry;
                return StringUtils.containsIgnoreCase(entry.getName(), keyword)||
                        StringUtils.containsIgnoreCase(entry.getNote(), keyword)||
                        StringUtils.containsIgnoreCase(entry.getComment(), keyword)||
                        StringUtils.containsIgnoreCase(entry.getLocation(), keyword);
            }

            return false;
        });
    }

    private void closeButtonClicked(ActionEvent actionEvent) {
        this.close();
    }

    public void clear() {
        this.txtKeyword.setText("");
        this.txtKeyword.requestFocus();
    }

    ///////////////////////////////////////////////

    private static abstract class SearchItem {

        @Override
        public abstract String toString();
    }

    private static class EntrySearchItem extends SearchItem {

        Entry entry;

        public EntrySearchItem(Entry entry) {
            this.entry = entry;
        }

        @Override
        public String toString() {
            return entry.getName();
        }
    }

    private static class CategorySearchItem extends SearchItem {

        Category category;

        public CategorySearchItem(Category category) {
            this.category = category;
        }

        @Override
        public String toString() {
            return category.getName();
        }
    }
}
