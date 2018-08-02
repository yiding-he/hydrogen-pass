package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;

public class ExportDialog extends BasicDialog {

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

    }

    private void closeButtonClicked(ActionEvent actionEvent) {

    }
}
