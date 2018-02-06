package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import javafx.event.ActionEvent;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class EditCategoryDialog extends BasicDialog {

    public EditCategoryDialog() {
        new DialogBuilder()
                .logo(AppLogo.getLogo())
                .title("编辑分类")
                .onOkButtonClicked(this::okClicked)
                .applyTo(this);
    }

    private void okClicked(ActionEvent event) {

    }
}
