package com.hyd.pass.fx;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.system.ClipboardHelper;
import com.hyd.pass.App;
import com.hyd.pass.dialogs.EntryInfoDialog;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

import static com.hyd.fx.builders.ImageBuilder.image;
import static com.hyd.fx.builders.MenuBuilder.*;

/**
 * @author yidin
 */
public class EntryTableRow extends TableRow<Entry> {

    private ContextMenu currentContextMenu;

    private ContextMenu createContextMenu() {

        if (currentContextMenu != null) {
            currentContextMenu.hide();
        }

        String location = this.getItem().getLocation();
        if (StringUtils.isNotBlank(location)) {

            MenuItem[] locationMenuItems = Stream.of(location.split(","))
                    .filter(StringUtils::isNotBlank)
                    .map(String::trim)
                    .map(l -> menuItem(l, () -> ClipboardHelper.putString(l)))
                    .toArray(MenuItem[]::new);

            currentContextMenu = contextMenu(
                    menuItem("编辑...", image("/icons/edit.png"), this::editEntryClicked),
                    menuItem("删除", image("/icons/delete.png"), this::deleteEntryClicked),
                    menu("复制地址", image("/icons/copy.png"), locationMenuItems)
            );

        } else {
            currentContextMenu = contextMenu(
                    menuItem("编辑...", this::editEntryClicked),
                    menuItem("删除", this::deleteEntryClicked)
            );
        }

        return currentContextMenu;
    }

    public EntryTableRow() {

        setOnContextMenuRequested(event -> {
            if (!isEmpty()) {
                createContextMenu().show(this, event.getScreenX(), event.getScreenY());
            }
        });

        setOnMouseClicked(event -> {
            if (!isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editEntryClicked();
            }
        });
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
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            App.setPasswordLibChanged();
            getTableView().refresh();
            getTableView().sort();
        }
    }
}
