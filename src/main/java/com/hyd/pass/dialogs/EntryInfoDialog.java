package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.model.Entry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yidin
 */
public class EntryInfoDialog extends BasicDialog {

    private Entry entry;

    @FXML
    public TextField txtEntryName;

    @FXML
    public TextField txtEntryLocation;

    @FXML
    public TextField txtEntryComment;

    public EntryInfoDialog(Entry entry) {
        this.entry = entry;

        new DialogBuilder()
                .title("创建入口")
                .logo(AppLogo.getLogo())
                .body("/fxml/entry-info-dialog.fxml", this)
                .buttons(ButtonType.OK, ButtonType.CANCEL)
                .onOkButtonClicked(this::onOkButtonClicked)
                .applyTo(this);
    }

    public Entry getEntry() {
        return entry;
    }

    private void onOkButtonClicked(ActionEvent event) {
        if (StringUtils.isBlank(txtEntryName.getText())) {
            event.consume();
        }

        this.entry = new Entry(
                StringUtils.trim(txtEntryName.getText()),
                StringUtils.trim(txtEntryLocation.getText()),
                StringUtils.trim(txtEntryComment.getText())
        );
    }
}
