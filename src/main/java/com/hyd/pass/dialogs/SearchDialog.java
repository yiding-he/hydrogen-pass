package com.hyd.pass.dialogs;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.cells.ListCellFactoryBuilder;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.pass.App;
import com.hyd.pass.model.Entry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yidin
 */
public class SearchDialog extends BasicDialog {

    @FXML
    private TextField txtKeyword;

    @FXML
    private ListView<SearchItem> lvEntryList;

    @FXML
    private Button btnClear;

    private Entry selectedEntry;

    public SearchDialog() {
        new DialogBuilder()
                .body("/fxml/search.fxml", this)
                .owner(AppPrimaryStage.getPrimaryStage())
                .title("关键字搜索")
                .buttons(ButtonType.CLOSE)
                .onButtonClicked(ButtonType.CLOSE, this::closeButtonClicked)
                .onStageShown(this::onStageShown)
                .applyTo(this);
    }

    public Entry getSelectedEntry() {
        return selectedEntry;
    }

    private void onStageShown(DialogEvent dialogEvent) {
        this.txtKeyword.requestFocus();
    }

    public void initialize() {
        this.btnClear.setOnAction(event -> this.clear());
        this.txtKeyword.textProperty().addListener((ob, oldValue, newValue) -> this.keywordChanged(newValue));

        new ListCellFactoryBuilder<SearchItem>()
                .setToString(SearchItem::toString)
                .setOnDoubleClick(this::searchItemSelected)
                .setTo(this.lvEntryList);
    }

    private void searchItemSelected(SearchItem item) {
        if (item instanceof EntrySearchItem) {
            entrySelected(((EntrySearchItem) item).entry);
        }
    }

    private void entrySelected(Entry entry) {
        this.selectedEntry = entry;
        this.close();
    }

    private void keywordChanged(String keyword) {

        if (StringUtils.isBlank(keyword)) {
            lvEntryList.getItems().clear();
            return;
        }

        List<Entry> entryList = new ArrayList<>();

        App.getPasswordLib().getRootCategory().iterateChildren(category -> {
            category.getEntries().stream()
                    .filter(entry -> entry.matchKeyword(keyword))
                    .forEach(entryList::add);
        });

        List<EntrySearchItem> searchResult = entryList.stream()
                .map(entry -> new EntrySearchItem(keyword, entry))
                .collect(Collectors.toList());

        lvEntryList.getItems().setAll(searchResult);
    }

    private void closeButtonClicked(ActionEvent actionEvent) {
        this.close();
    }

    public void clear() {
        this.txtKeyword.setText("");
        this.txtKeyword.requestFocus();
    }

    ///////////////////////////////////////////////

    private static abstract class SearchItem {

        String keyword;

        public SearchItem(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public abstract String toString();

        // 在多行文本中搜索关键字，仅返回包含关键字的一行
        String findContent(String content) {
            if (content == null) {
                return null;
            }

            return Stream.of(content.split("\n"))
                    .filter(Objects::nonNull)
                    .filter(line -> line.contains(keyword))
                    .findFirst().orElse(null);
        }

        String generateToString(String[] names, String[] searchContents) {
            List<String> items = new ArrayList<>();

            for (String name : names) {
                items.add("[" + name + "]");
            }

            for (String content : searchContents) {
                items.add(findContent(content));
            }

            items.removeAll(Collections.singleton(null));
            return String.join(" ", items);
        }
    }

    private static class EntrySearchItem extends SearchItem {

        Entry entry;

        public EntrySearchItem(String keyword, Entry entry) {
            super(keyword);
            this.entry = entry;
        }

        @Override
        public String toString() {
            return generateToString(
                    new String[]{entry.getName(), entry.getLocation()},
                    new String[]{entry.getComment(), entry.getNote()}
            );
        }
    }
}
