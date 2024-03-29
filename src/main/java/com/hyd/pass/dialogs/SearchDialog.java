package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.cells.TreeCellFactory;
import com.hyd.fx.components.FilterableTreeView;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.App;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import com.hyd.pass.model.SearchItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import static com.hyd.fx.builders.ImageBuilder.image;
import static com.hyd.pass.utils.Str.containsIgnoreCase;

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
                .body(getClass().getResource("/fxml/search.fxml"), this)
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

        this.tvSearchResult.setCellFactory(new TreeCellFactory<SearchItem>()
                .setToString(SearchItem::toString)
                .setOnDoubleClick(this::searchItemSelected)
                .setIconSupplier(this::getTreeNodeIcon)
        );
    }

    private Image getTreeNodeIcon(TreeItem<SearchItem> treeItem) {
        if (treeItem.getValue() instanceof SearchItem.CategorySearchItem) {
            return image("/icons/folder.png");
        } else if (treeItem.getValue() instanceof SearchItem.EntrySearchItem) {
            return image("/icons/pc.png");
        } else {
            return null;
        }
    }

    private TreeItem<SearchItem> buildOriginalRoot() {
        Category rootCategory = App.getPasswordLib().getRootCategory();
        TreeItem<SearchItem> rootTreeItem = new TreeItem<>(new SearchItem.CategorySearchItem(rootCategory));

        buildOriginalRoot(rootCategory, rootTreeItem);
        return rootTreeItem;
    }

    private void buildOriginalRoot(Category parentCategory, TreeItem<SearchItem> parentTreeItem) {

        parentTreeItem.setExpanded(true);

        for (Category child : parentCategory.getChildren()) {
            TreeItem<SearchItem> childTreeItem = new TreeItem<>(new SearchItem.CategorySearchItem(child));
            parentTreeItem.getChildren().add(childTreeItem);
            buildOriginalRoot(child, childTreeItem);
        }

        for (Entry entry : parentCategory.getEntries()) {
            TreeItem<SearchItem> childTreeItem = new TreeItem<>(new SearchItem.EntrySearchItem(entry));
            parentTreeItem.getChildren().add(childTreeItem);
        }
    }

    private void searchItemSelected(SearchItem item) {
        if (item instanceof SearchItem.EntrySearchItem) {
            entrySelected(((SearchItem.EntrySearchItem) item).entry);
        }
    }

    private void entrySelected(Entry entry) {
        this.selectedEntry = entry;
        this.close();
    }

    private void keywordChanged(String keyword) {
        this.tvSearchResult.filter(searchItem -> {

            if (searchItem instanceof SearchItem.CategorySearchItem) {
                return ((SearchItem.CategorySearchItem) searchItem).category.getName().contains(keyword);

            } else if (searchItem instanceof SearchItem.EntrySearchItem) {
                Entry entry = ((SearchItem.EntrySearchItem) searchItem).entry;
                return containsIgnoreCase(entry.getName(), keyword) ||
                        containsIgnoreCase(entry.getNote(), keyword) ||
                        containsIgnoreCase(entry.getComment(), keyword) ||
                        containsIgnoreCase(entry.getLocation(), keyword);
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

}
