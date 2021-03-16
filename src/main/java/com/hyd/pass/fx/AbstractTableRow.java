package com.hyd.pass.fx;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;

public abstract class AbstractTableRow<T> extends TableRow<T> {

    protected ContextMenu contextMenu;

    public AbstractTableRow() {
        setOnContextMenuRequested(event -> {
            if (!isEmpty()) {
                this.contextMenu = createContextMenu();
                this.contextMenu.show(this, event.getScreenX(), event.getScreenY());
                event.consume();
            }
        });
        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                onDoubleClick();
            } else if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY && contextMenu != null) {
                contextMenu.hide();
            }
        });
        focusedProperty().addListener((ob, old, focused) -> {
            if (!focused && contextMenu != null) {
                contextMenu.hide();
            }
        });
    }

    abstract ContextMenu createContextMenu();

    abstract void onDoubleClick();
}
