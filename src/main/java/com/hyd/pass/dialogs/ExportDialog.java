package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.fx.helpers.TreeViewHelper;
import com.hyd.pass.App;
import com.hyd.pass.fx.TreeItemBuilder;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.SearchItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;

public class ExportDialog extends BasicDialog {

    @FXML
    private TreeView<SearchItem> tvEntries;

    public static final ButtonType BUTTON_TYPE_EXPORT =
            new ButtonType("导出...", ButtonBar.ButtonData.YES);

    public ExportDialog() {
        new DialogBuilder()
                .body("/fxml/export.fxml", this)
                .owner(AppPrimaryStage.getPrimaryStage())
                .title("导出密码库")
                .buttons(BUTTON_TYPE_EXPORT, ButtonType.CLOSE)
                .onButtonClicked(ButtonType.CLOSE, this::closeButtonClicked)
                .onButtonClicked(BUTTON_TYPE_EXPORT, this::exportButtonClicked)
                .onStageShown(this::onStageShown)
                .applyTo(this);
    }

    private void exportButtonClicked(ActionEvent actionEvent) {

        TreeItem<SearchItem> export =
                TreeViewHelper.buildSubTree(tvEntries.getRoot(), SearchItem::isSelected);

        TreeViewHelper.iterate(export, treeItem -> {
            SearchItem searchItem = treeItem.getValue();
            System.out.println(searchItem + ":" + searchItem.isSelected());
            return true;
        });

        actionEvent.consume();
    }

    private void onStageShown(DialogEvent dialogEvent) {
        Category rootCategory = App.getPasswordLib().getRootCategory();
        tvEntries.setRoot(TreeItemBuilder.buildSearchItemTree(rootCategory));
        tvEntries.setCellFactory(CheckBoxTreeCell.forTreeView());
    }

    private void closeButtonClicked(ActionEvent actionEvent) {

    }
}
