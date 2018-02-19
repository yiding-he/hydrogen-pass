package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import org.apache.commons.lang3.StringUtils;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class EnterPasswordDialog extends BasicDialog {

    @FXML
    private PasswordField mainPassword;

    private String fileName;

    private String password;

    public EnterPasswordDialog(String fileName) {
        new DialogBuilder()
                .title("输入“" + fileName + "”的主密码")
                .logo(AppLogo.getLogo())
                .body("/fxml/enter-password.fxml", this)
                .buttons(ButtonType.OK, ButtonType.CANCEL)
                .onOkButtonClicked(this::onOkButtonClicked)
                .onStageShown(event -> mainPassword.requestFocus())
                .applyTo(this);
    }

    private void onOkButtonClicked(ActionEvent event) {
        if (StringUtils.isBlank(mainPassword.getText())) {
            AlertDialog.error("密码不能为空");
            event.consume();
            return;
        }

        this.password = mainPassword.getText();
    }

    public String getPassword() {
        return password;
    }
}
