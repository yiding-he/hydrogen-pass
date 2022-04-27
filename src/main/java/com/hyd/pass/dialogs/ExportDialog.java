package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.*;
import com.hyd.fx.helpers.TreeViewHelper;
import com.hyd.pass.App;
import com.hyd.pass.fx.TreeItemBuilder;
import com.hyd.pass.model.*;
import com.hyd.pass.utils.Str;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;

import java.io.File;

public class ExportDialog extends BasicDialog {

    @FXML
    private TreeView<SearchItem> tvEntries;

    @FXML
    private PasswordField pwdMasterPassword;

    @FXML
    private TextField txtSavePath;

    public static final ButtonType BUTTON_TYPE_EXPORT =
            new ButtonType("导出...", ButtonBar.ButtonData.YES);

    public ExportDialog() {
        new DialogBuilder()
                .body(getClass().getClassLoader().getResource("fxml/export.fxml"), this)
                .owner(AppPrimaryStage.getPrimaryStage())
                .title("导出密码库")
                .buttons(BUTTON_TYPE_EXPORT, ButtonType.CLOSE)
                .onButtonClicked(ButtonType.CLOSE, this::closeButtonClicked)
                .onButtonClicked(BUTTON_TYPE_EXPORT, this::exportButtonClicked)
                .onStageShown(this::onStageShown)
                .applyTo(this);
    }

    private void exportButtonClicked(ActionEvent actionEvent) {

        TreeItem<SearchItem> rootEntry =
                TreeViewHelper.buildSubTree(tvEntries.getRoot(), SearchItem::isSelected);

        String password = pwdMasterPassword.getText();
        String savePath = txtSavePath.getText();

        if (rootEntry == null) {
            AlertDialog.error("导出", "请选择要导出的节点。");
            actionEvent.consume();
            return;
        }

        if (Str.isBlank(password)) {
            AlertDialog.error("导出", "请填写主密码。");
            actionEvent.consume();
            return;
        }

        if (Str.isBlank(savePath)) {
            AlertDialog.error("导出", "请选择要导出的文件位置。");
            actionEvent.consume();
            return;
        }

        try {
            exportPasswordLib(rootEntry, password, savePath);
            AlertDialog.info("导出完毕", "导出完毕。");
        } catch (Exception e) {
            AlertDialog.error("导出失败", e);
            actionEvent.consume();
        }
    }

    private void exportPasswordLib(TreeItem<SearchItem> rootEntry, String password, String savePath) {

        Category rootCategory = assembleCategory(rootEntry);
        PasswordLib passwordLib = new PasswordLib(new File(savePath), password, true);
        passwordLib.setRootCategory(rootCategory);
        passwordLib.save();
    }

    private Category assembleCategory(TreeItem<SearchItem> rootEntry) {
        Category origin = ((SearchItem.CategorySearchItem) rootEntry.getValue()).category;
        Category category = copyCategory(origin);

        copyChildren(rootEntry, category);

        return category;
    }

    private Category copyCategory(Category origin) {
        Category category = new Category();
        copyProperties(origin, category);
        return category;
    }

    private void copyChildren(TreeItem<SearchItem> treeItem, Category category) {

        treeItem.getChildren().forEach(childTreeItem -> {
            SearchItem searchItem = childTreeItem.getValue();

            if (searchItem instanceof SearchItem.CategorySearchItem) {
                Category origin = ((SearchItem.CategorySearchItem) searchItem).category;
                Category child = copyCategory(origin);
                category.addChild(child);

                copyChildren(childTreeItem, child);
            } else if (searchItem instanceof SearchItem.EntrySearchItem) {
                category.addEntry(((SearchItem.EntrySearchItem) searchItem).entry);
            }
        });
    }

    // 将基本属性从 origin 拷贝到 category
    private void copyProperties(Category origin, Category category) {
        category.setId(origin.getId());
        category.setName(origin.getName());
        category.setOrder(origin.getOrder());
        category.setSortBy(origin.getSortBy());
        category.setParentId(origin.getParentId());
    }

    private void onStageShown(DialogEvent dialogEvent) {
        Category rootCategory = App.getPasswordLib().getRootCategory();
        tvEntries.setRoot(TreeItemBuilder.buildSearchItemTree(rootCategory));
        tvEntries.setCellFactory(CheckBoxTreeCell.forTreeView());
    }

    private void closeButtonClicked(ActionEvent actionEvent) {
        this.close();
    }

    @FXML
    public void onSelectFileClicked() {
        File f = FileDialog.showSaveFile(
                AppPrimaryStage.getPrimaryStage(), "选择保存位置", App.FILE_EXT, App.FILE_EXT_NAME, "");

        if (f != null) {
            this.txtSavePath.setText(f.getAbsolutePath());
        }
    }
}
