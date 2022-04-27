package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.model.Entry;
import com.hyd.pass.utils.Str;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

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
                .title("入口信息")
                .logo(AppLogo.getLogo())
                .body(getClass().getClassLoader().getResource("fxml/entry-info-dialog.fxml"), this)
                .buttons(ButtonType.OK, ButtonType.CANCEL)
                .onOkButtonClicked(this::onOkButtonClicked)
                .onStageShown(event -> txtEntryName.requestFocus())
                .applyTo(this);
    }

    public void initialize() {
        if (this.entry != null) {
            this.txtEntryName.setText(this.entry.getName());
            this.txtEntryLocation.setText(this.entry.getLocation());
            this.txtEntryComment.setText(this.entry.getComment());
        }
    }

    public Entry getEntry() {
        return entry;
    }

    private void onOkButtonClicked(ActionEvent event) {

        if (Str.isBlank(txtEntryName.getText())) {
            event.consume();
            return;
        }

        String name = Str.trim(txtEntryName.getText());
        String location = Str.trim(txtEntryLocation.getText());
        String comment = Str.trim(txtEntryComment.getText());

        if (this.entry == null) {
            this.entry = new Entry(name, location, comment);
        } else {
            this.entry.setName(name);
            this.entry.setLocation(location);
            this.entry.setComment(comment);
        }
    }
}
