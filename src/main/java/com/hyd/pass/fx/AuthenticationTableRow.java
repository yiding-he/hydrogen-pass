package com.hyd.pass.fx;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.system.ClipboardHelper;
import com.hyd.pass.App;
import com.hyd.pass.dialogs.AuthenticationInfoDialog;
import com.hyd.pass.model.Authentication;
import com.hyd.pass.model.Entry;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;

import static com.hyd.fx.builders.ImageBuilder.imageView;
import static com.hyd.fx.builders.MenuBuilder.contextMenu;
import static com.hyd.fx.builders.MenuBuilder.menuItem;
import static com.hyd.fx.system.ClipboardHelper.putApplicationClipboard;

/**
 * @author yiding.he
 */
public class AuthenticationTableRow extends AbstractTableRow<Authentication> {

    public static final String AUTH_CLIP_KEY = "copy_authentication";

    public static final String ENTRY_CLIP_KEY = "copy_entry";

    private final ContextMenu contextMenu = contextMenu(
            menuItem("复制用户名", imageView("/icons/copy.png", 16), "Shortcut+X", this::copyUsernameClicked),
            menuItem("复制密码", imageView("/icons/copy.png", 16), "Shortcut+C", this::copyPasswordClicked),
            new SeparatorMenuItem(),
            menuItem("复制账号", imageView("/icons/copy.png", 16), this::copyEntryClicked),
            menuItem("编辑账号...", imageView("/icons/edit.png", 16), this::editEntryClicked),
            menuItem("删除账号", imageView("/icons/delete.png", 16), this::deleteEntryClicked)
    );

    private void copyEntryClicked() {
        putApplicationClipboard(AUTH_CLIP_KEY, this.getItem().clone());
    }

    public AuthenticationTableRow() {

    }

    @Override
    ContextMenu createContextMenu() {
        return contextMenu;
    }

    @Override
    void onDoubleClick() {
        editEntryClicked();
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
        dialog.setOwner(AppPrimaryStage.getPrimaryStage());
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
