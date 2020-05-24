package com.hyd.pass.fx;

import static com.hyd.fx.builders.ImageBuilder.imageView;
import static com.hyd.fx.builders.MenuBuilder.contextMenu;
import static com.hyd.fx.builders.MenuBuilder.menuItem;
import static com.hyd.fx.system.ClipboardHelper.putApplicationClipboard;

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

/**
 * @author yiding.he
 */
public class AuthenticationTableRow extends TableRow<Authentication> {

    public static final String AUTH_CLIP_KEY = "copy_authentication";

    public static final String ENTRY_CLIP_KEY = "copy_entry";

    private final ContextMenu contextMenu = contextMenu(
            menuItem("复制用户名", imageView("/icons/copy.png"), "Shortcut+X", this::copyUsernameClicked),
            menuItem("复制密码", imageView("/icons/copy.png"), "Shortcut+C", this::copyPasswordClicked),
            new SeparatorMenuItem(),
            menuItem("复制账号", imageView("/icons/copy.png"), this::copyEntryClicked),
            menuItem("编辑账号...", imageView("/icons/edit.png"), this::editEntryClicked),
            menuItem("删除账号", imageView("/icons/delete.png"), this::deleteEntryClicked)
    );

    private void copyEntryClicked() {
        putApplicationClipboard(AUTH_CLIP_KEY, this.getItem().clone());
    }

    public AuthenticationTableRow() {

        setOnContextMenuRequested(event -> {
            if (!isEmpty()) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
                event.consume();
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
