package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.model.Authentication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.security.SecureRandom;
import java.util.Random;

import static com.hyd.pass.utils.Str.isBlank;
import static com.hyd.pass.utils.Str.trim;


/**
 * @author yiding.he
 */
@SuppressWarnings("unused")
public class AuthenticationInfoDialog extends BasicDialog {

    private Authentication authentication;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextArea txtPassword;

    @FXML
    private CheckBox chkNum;

    @FXML
    private CheckBox chkSml;

    @FXML
    private CheckBox chkBig;

    @FXML
    private CheckBox chkSpc;

    @FXML
    private CheckBox chkCfs;

    @FXML
    private Spinner<Integer> spnLength;

    private final Random random = new SecureRandom();

    public void onGenerateClick() {
        String chars = "";
        if (chkNum.isSelected()) {
            chars += "0123456789";
        }
        if (chkSml.isSelected()) {
            chars += "abcdefghijklmnopqrstuvwxyz";
        }
        if (chkBig.isSelected()) {
            chars += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        if (chkSpc.isSelected()) {
            chars += "~!#$%^&*()_+`-=[]{}\\|;':\",./<>?";
        }
        if (chkCfs.isSelected()) {
            chars = chars.replaceAll("[0oO1Ilq9QB85sSuvUVZ2]", "");
        }

        int length = spnLength.getValue();
        char[] selection = chars.toCharArray();
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = selection[random.nextInt(selection.length)];
        }

        txtPassword.setText(new String(result));
    }

    public AuthenticationInfoDialog(Authentication authentication) {
        this.authentication = authentication;

        new DialogBuilder()
            .title("登录信息")
            .logo(AppLogo.getLogo())
            .body("/fxml/authentication-info-dialog.fxml", this)
            .buttons(ButtonType.OK, ButtonType.CANCEL)
            .onOkButtonClicked(this::onOkButtonClicked)
            .onStageShown(event -> txtUsername.requestFocus())
            .resizable(true)
            .applyTo(this);

        // make textarea grows vertically
        this.txtPassword.prefHeightProperty().bind(((HBox)this.txtPassword.getParent()).heightProperty());
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
