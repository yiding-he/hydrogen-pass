package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.App;
import com.hyd.pass.fx.TreeItemBuilder;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.SearchItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.TreeView;

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
                .onStageShown(this::onStageShown)
                .applyTo(this);
    }

    private void onStageShown(DialogEvent dialogEvent) {
        Category rootCategory = App.getPasswordLib().getRootCategory();
        tvEntries.setRoot(TreeItemBuilder.buildSearchItemTree(rootCategory));
    }

    private void closeButtonClicked(ActionEvent actionEvent) {

    }
}
