package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.fx.dialog.FileDialog;
import com.hyd.pass.App;
import com.hyd.pass.utils.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Objects;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class CreatePasswordLibDialog extends BasicDialog {

    @FXML
    private TextField txtSavePath;

    @FXML
    private PasswordField password1;

    @FXML
    private PasswordField password2;

    public CreatePasswordLibDialog() {
        new DialogBuilder()
                .title("创建密码库")
                .logo(AppLogo.getLogo())
                .body("/fxml/create-password-lib.fxml", this)
                .buttons(ButtonType.OK, ButtonType.CANCEL)
                .onOkButtonClicked(this::onOkButtonClicked)
                .applyTo(this);
    }

    private void onOkButtonClicked(ActionEvent event) {

        if (StringUtils.isBlank(txtSavePath.getText())) {
            AlertDialog.error("存储文件不能为空");
            event.consume();
            return;
        }

        if (!FileUtils.ensureFile(txtSavePath.getText())) {
            AlertDialog.error("无法在目标位置创建文件，请选择其他位置");
            event.consume();
            return;
        }

        if (StringUtils.isAllBlank(password1.getText(), password2.getText())) {
            AlertDialog.error("主密码不能为空");
            event.consume();
            return;
        }

        if (!Objects.equals(password1.getText(), password2.getText())) {
            AlertDialog.error("主密码不一致");
            event.consume();
            return;
        }

        this.saveFile = new File(this.txtSavePath.getText());
        this.masterPassword = this.password1.getText();
    }

    private File saveFile;

    private String masterPassword;

    @FXML
    public void onSelectFileClicked() {
        File f = FileDialog.showSaveFile(
                AppPrimaryStage.getPrimaryStage(), "选择保存位置", App.FILE_EXT, App.FILE_EXT_NAME, "");

        if (f != null) {
            this.txtSavePath.setText(f.getAbsolutePath());
        }
    }

    public File getSaveFile() {
        return saveFile;
    }

    public String getMasterPassword() {
        return masterPassword;
    }
}
