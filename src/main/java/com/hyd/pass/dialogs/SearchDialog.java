package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.model.Authentication;
import com.hyd.pass.model.Entry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @author yidin
 */
public class SearchDialog extends BasicDialog {

    @FXML
    private TextField txtKeyword;

    @FXML
    private ListView<SearchItem> lvEntryList;

    @FXML
    private ListView<SearchItem> lvAuthenticationList;

    @FXML
    private Button btnClear;

    private Entry selectedEntry;

    private Authentication selectedAuthentication;

    public SearchDialog() {
        new DialogBuilder()
                .body("/fxml/search.fxml", this)
                .owner(AppPrimaryStage.getPrimaryStage())
                .title("关键字搜索（尚未实现）")
                .buttons(ButtonType.CLOSE)
                .onButtonClicked(ButtonType.CLOSE, this::closeButtonClicked)
                .applyTo(this);
    }

    public void initialize() {
        this.btnClear.setOnAction(event -> this.clear());
    }

    private void closeButtonClicked(ActionEvent actionEvent) {
        this.close();
    }

    public void clear() {
        this.txtKeyword.setText("");
        this.txtKeyword.requestFocus();
    }

    ///////////////////////////////////////////////

    private static class SearchItem {

    }
}
