package com.hyd.pass.fx;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.system.ClipboardHelper;
import com.hyd.pass.App;
import com.hyd.pass.dialogs.AuthenticationInfoDialog;
import com.hyd.pass.model.Authentication;
import com.hyd.pass.model.Entry;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;

import static com.hyd.fx.components.MenuBuilder.contextMenu;
import static com.hyd.fx.components.MenuBuilder.menuItem;

/**
 * @author yiding.he
 */
public class AuthenticationTableRow extends TableRow<Authentication> {

    private ContextMenu contextMenu = contextMenu(
            menuItem("复制用户名", "Shortcut+X", this::copyUsernameClicked),
            menuItem("复制密码", "Shortcut+C", this::copyPasswordClicked),
            new SeparatorMenuItem(),
            menuItem("编辑...", this::editEntryClicked),
            menuItem("删除", this::deleteEntryClicked)
    );

    public AuthenticationTableRow() {

        setOnContextMenuRequested(event -> {
            if (!isEmpty()) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            }
        });

        setOnMouseClicked(event -> {
            if (!isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editEntryClicked();
            }
        });
    }

    private void deleteEntryClicked() {
        if (AlertDialog.confirmYesNo("删除登录", "确定要删除“" + getItem().getUsername() + "”吗？")) {
            Entry currentEntry = App.getCurrentEntry();
            if (currentEntry != null) {
                currentEntry.getAuthentications().remove(getItem());
                getTableView().getItems().remove(getItem());
                App.setPasswordLibChanged();
            }
        }
    }

    private void editEntryClicked() {
        AuthenticationInfoDialog dialog = new AuthenticationInfoDialog(getItem());
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            App.setPasswordLibChanged();
            getTableView().refresh();
            getTableView().sort();
        }
    }

    private void copyUsernameClicked() {
        ClipboardHelper.putString(getItem().getUsername());
    }

    private void copyPasswordClicked() {
        ClipboardHelper.putString(getItem().getPassword());
    }
}
