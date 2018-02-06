package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class EditCategoryDialog extends BasicDialog {

    @FXML
    public TextField txtCategoryName;

    private String categoryName;

    public EditCategoryDialog(String title, String defaultName) {
        new DialogBuilder()
                .logo(AppLogo.getLogo())
                .body("/fxml/edit-category.fxml", this)
                .title(title)
                .onOkButtonClicked(this::okClicked)
                .onStageShown(event -> {
                    txtCategoryName.requestFocus();
                })
                .applyTo(this);

        this.categoryName = defaultName;
        this.txtCategoryName.setText(this.categoryName);
    }

    private void okClicked(ActionEvent event) {
        if (StringUtils.isBlank(this.txtCategoryName.getText())) {
            AlertDialog.error("分类名称不能为空");
            event.consume();
            return;
        }

        this.categoryName = txtCategoryName.getText();
    }

    public String getCategoryName() {
        return categoryName;
    }
}
