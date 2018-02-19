package com.hyd.pass.fx;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.pass.App;
import com.hyd.pass.dialogs.EntryInfoDialog;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;

import static com.hyd.fx.components.MenuBuilder.contextMenu;
import static com.hyd.fx.components.MenuBuilder.menuItem;

/**
 * @author yidin
 */
public class EntryTableRow extends TableRow<Entry> {

    private ContextMenu contextMenu = contextMenu(
            menuItem("编辑...", this::editEntryClicked),
            menuItem("删除", this::deleteEntryClicked)
    );

    public EntryTableRow() {

        setOnContextMenuRequested(event -> {
            if (!isEmpty()) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            }
        });

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
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
