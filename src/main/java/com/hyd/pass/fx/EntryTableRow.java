package com.hyd.pass.fx;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.system.ClipboardHelper;
import com.hyd.pass.App;
import com.hyd.pass.dialogs.EntryInfoDialog;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.List;

import static com.hyd.fx.builders.ImageBuilder.imageView;
import static com.hyd.fx.builders.MenuBuilder.*;
import static com.hyd.fx.system.ClipboardHelper.putApplicationClipboard;
import static com.hyd.pass.fx.AuthenticationTableRow.ENTRY_CLIP_KEY;

/**
 * @author yidin
 */
public class EntryTableRow extends AbstractTableRow<Entry> {

    private ContextMenu currentContextMenu;

    @Override
    ContextMenu createContextMenu() {


        currentContextMenu = contextMenu(
            menuItem("编辑入口...", imageView("/icons/edit.png", 16), this::editEntryClicked),
            menuItem("复制入口", imageView("/icons/copy.png", 16), this::copyEntryClicked),
            menuItem("删除入口", imageView("/icons/delete.png", 16), this::deleteEntryClicked)
        );

        List<String> locationList = this.getItem().locationAsList();
        if (!locationList.isEmpty()) {
            currentContextMenu.getItems().add(menu(
                "复制地址",
                imageView("/icons/copy.png", 16),
                locationList.stream()
                    .map(l -> menuItem(l, () -> ClipboardHelper.putString(l)))
                    .toArray(MenuItem[]::new)
            ));
        }

        return currentContextMenu;
    }

    public EntryTableRow() {

    }

    @Override
    void onDoubleClick() {
        editEntryClicked();
    }

    private void copyEntryClicked() {
        putApplicationClipboard(ENTRY_CLIP_KEY, this.getItem().clone());
    }

    private void deleteEntryClicked() {
        if (AlertDialog.confirmYesNo("删除项", "确定要删除“" + getItem().getName() + "”吗？")) {
            Category currentCategory = App.getCurrentCategory();
            if (currentCategory != null) {
                currentCategory.removeEntry(getItem());
                getTableView().getItems().remove(getItem());
                App.setPasswordLibChanged();
            }
        }
    }

    private void editEntryClicked() {
        EntryInfoDialog dialog = new EntryInfoDialog(getItem());
        dialog.setOwner(AppPrimaryStage.getPrimaryStage());
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            App.setPasswordLibChanged();
            getTableView().refresh();
            getTableView().sort();
        }
    }
}
