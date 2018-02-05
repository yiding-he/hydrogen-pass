package com.hyd.pass.controllers;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.DialogCreator;
import com.hyd.pass.Logger;

import java.io.IOException;

/**
 * @author yiding.he
 */
public class MainController {

    private static final Logger logger = Logger.getLogger(MainController.class);

    public void openFileClicked() {
        try {
            OpenRepositoryController controller = DialogCreator.openDialog("/fxml/open-repository-dialog.fxml");

        } catch (IOException e) {
            logger.error("", e);
            AlertDialog.error("错误", "打开对话框失败");
        }
    }
}
