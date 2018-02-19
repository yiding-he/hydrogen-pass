package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.model.Authentication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * @author yiding.he
 */
public class AuthenticationInfoDialog extends BasicDialog {

    private Authentication authentication;

    @FXML
    public TextField txtUsername;

    @FXML
    public TextField txtPassword;

    public AuthenticationInfoDialog(Authentication authentication) {
        this.authentication = authentication;

        new DialogBuilder()
                .title("登录信息")
                .logo(AppLogo.getLogo())
                .body("/fxml/authentication-info-dialog.fxml", this)
                .buttons(ButtonType.OK, ButtonType.CANCEL)
                .onOkButtonClicked(this::onOkButtonClicked)
                .onStageShown(event -> txtUsername.requestFocus())
                .applyTo(this);
    }

    public void initialize() {
        if (this.authentication != null) {
            this.txtUsername.setText(this.authentication.getUsername());
            this.txtPassword.setText(this.authentication.getPassword());
        }
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    private void onOkButtonClicked(ActionEvent actionEvent) {
        if (isBlank(txtUsername.getText())) {
            actionEvent.consume();
            return;
        }

        String username = trim(txtUsername.getText());
        String password = trim(txtPassword.getText());

        if (this.authentication == null) {
            this.authentication = new Authentication(username, password);
        } else {
            this.authentication.setUsername(username);
            this.authentication.setPassword(password);
        }
    }
}
