package com.hyd.pass.controllers;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.FileDialog;
import com.hyd.pass.App;
import com.hyd.pass.Logger;
import com.hyd.pass.dialogs.CreatePasswordDialog;
import com.hyd.pass.dialogs.EnterPasswordDialog;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.PasswordLib;
import com.hyd.pass.model.PasswordLibException;
import javafx.scene.control.*;

import java.io.File;

/**
 * @author yiding.he
 */
public class MainController extends BaseController {

    private static final Logger logger = Logger.getLogger(MainController.class);

    public SplitPane split1;

    public SplitPane split2;

    public TreeView<Category> tvCategories;

    public TableView tblEntries;

    public TableView tvAuthentications;

    public void initialize() {
        this.split1.setDividerPositions(0.2);
        this.split2.setDividerPositions(0.4);
    }

    public void openFileClicked() {
        File file = FileDialog.showOpenFile(
                AppPrimaryStage.getPrimaryStage(), "打开密码库文件", App.FILE_EXT, App.FILE_EXT_NAME);

        if (file != null) {
            EnterPasswordDialog dialog = new EnterPasswordDialog();
            ButtonType buttonType = dialog.showAndWait().orElse(ButtonType.CANCEL);

            if (buttonType == ButtonType.OK) {
                try {
                    PasswordLib passwordLib = new PasswordLib(file, dialog.getPassword(), false);
                    loadPasswordLib(passwordLib);
                    App.setPasswordLib(passwordLib);
                } catch (PasswordLibException e) {
                    AlertDialog.error("密码不正确");
                }
            }
        }
    }

    public void newFileClicked() {
        runSafe(this::newFileClicked0);
    }

    private void newFileClicked0() throws Exception {
        CreatePasswordDialog createPasswordDialog = new CreatePasswordDialog();
        ButtonType buttonType = createPasswordDialog.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType == ButtonType.OK) {
            try {
                createPasswordLib(createPasswordDialog);
            } catch (Exception e) {
                logger.error("创建密码库失败", e);
                AlertDialog.error("创建密码库失败");
            }
        }
    }

    private void createPasswordLib(CreatePasswordDialog createPasswordDialog) {
        PasswordLib passwordLib = new PasswordLib(
                createPasswordDialog.getSaveFile(),
                createPasswordDialog.getMasterPassword(),
                true
        );

        passwordLib.save();
        loadPasswordLib(passwordLib);
        App.setPasswordLib(passwordLib);
    }

    private void loadPasswordLib(PasswordLib passwordLib) {
        loadCategories(passwordLib);
    }

    private void loadCategories(PasswordLib passwordLib) {
        this.tvCategories.setRoot(new TreeItem<>(passwordLib.getRootCategory()));
    }
}
