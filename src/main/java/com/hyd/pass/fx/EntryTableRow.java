package com.hyd.pass.fx;

import com.hyd.pass.model.Entry;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;

import static com.hyd.fx.components.MenuBuilder.contextMenu;
import static com.hyd.fx.components.MenuBuilder.menuItem;

/**
 * @author yidin
 */
public class EntryTableRow extends TableRow<Entry> {

    private ContextMenu contextMenu = contextMenu(

    );
}
