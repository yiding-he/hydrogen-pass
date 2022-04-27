package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppLogo;
import com.hyd.fx.dialog.*;
import com.hyd.pass.utils.Str;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
                .body(getClass().getClassLoader().getResource("fxml/edit-category.fxml"), this)
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
        if (Str.isBlank(this.txtCategoryName.getText())) {
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
