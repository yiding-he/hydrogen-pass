package com.hyd.pass.controllers;

import static com.hyd.fx.helpers.TableViewHelper.setColumnValueFactory;
import static com.hyd.fx.system.ClipboardHelper.getApplicationClipboard;
import static com.hyd.pass.fx.AuthenticationTableRow.AUTH_CLIP_KEY;
import static com.hyd.pass.fx.AuthenticationTableRow.ENTRY_CLIP_KEY;
import static javafx.scene.control.TableColumn.SortType.ASCENDING;
import static javafx.scene.control.TableColumn.SortType.DESCENDING;

import com.hyd.fx.NodeUtils;
import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.FileDialog;
import com.hyd.fx.system.ClipboardHelper;
import com.hyd.pass.App;
import com.hyd.pass.Logger;
import com.hyd.pass.conf.UserConfig;
import com.hyd.pass.dialogs.AuthenticationInfoDialog;
import com.hyd.pass.dialogs.ChangeMasterPasswordDialog;
import com.hyd.pass.dialogs.CreatePasswordLibDialog;
import com.hyd.pass.dialogs.EnterPasswordDialog;
import com.hyd.pass.dialogs.EntryInfoDialog;
import com.hyd.pass.dialogs.ExportDialog;
import com.hyd.pass.dialogs.SearchDialog;
import com.hyd.pass.fx.AuthenticationTableRow;
import com.hyd.pass.fx.CategoryTreeView;
import com.hyd.pass.fx.EntryTableRow;
import com.hyd.pass.model.Authentication;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import com.hyd.pass.model.PasswordLib;
import com.hyd.pass.model.PasswordLibException;
import com.hyd.pass.utils.OrElse;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SortEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yiding.he
 */
public class MainController extends BaseController {

    private static final Logger logger = Logger.getLogger(MainController.class);

    public SplitPane split1;

    public SplitPane split2;

    public CategoryTreeView tvCategories;

    public TableView<Entry> tblEntries;

    public TableView<Authentication> tblAuthentications;

    public Button btnNewEntry;

    public Button btnNewLogin;

    public TextArea txtNote;

    public TabPane tpEntryInfo;

    public TableColumn<Entry, String> colEntryName;

    public TableColumn<Entry, String> colEntryLocation;

    public TableColumn<Entry, String> colEntryCreateTime;

    public TableColumn<Entry, String> colEntryComment;

    public TableColumn<Authentication, String> colAuthUsername;

    public TableColumn<Authentication, String> colAuthPassword;

    public CheckMenuItem mnuAutoSave;

    public CheckMenuItem mnuAutoOpen;

    public CheckMenuItem mnuNoteWrap;

    public MenuItem mnuPasteAuthentication;

    public MenuItem mnuPasteEntry;

    public MenuItem mnuExport;

    public Label lblStatus;

    public void initialize() {
        this.split1.setDividerPositions(0.2);
        this.split2.setDividerPositions(0.4);

        ////////////////////////////////////////////////////////////////////////////////

        ObservableValue<TreeItem<Category>> selectedCategoryItem =
                this.tvCategories.getSelectionModel().selectedItemProperty();

        selectedCategoryItem.addListener(this::selectedCategoryChanged);

        ////////////////////////////////////////////////////////////////////////////////

        setColumnValueFactory(colEntryName, Entry::getName);
        setColumnValueFactory(colEntryLocation, Entry::getLocation);
        setColumnValueFactory(colEntryCreateTime, Entry::getCreateTime);
        setColumnValueFactory(colEntryComment, Entry::getComment);

        this.tblEntries.setRowFactory(tv -> new EntryTableRow());
        this.tblEntries.getSortOrder().add(colEntryName);
        this.tblEntries.setOnSort(this::onEntryTableSort);

        ////////////////////////////////////////////////////////////////////////////////

        ReadOnlyObjectProperty<Entry> selectedEntry =
                this.tblEntries.getSelectionModel().selectedItemProperty();

        selectedEntry.addListener(this::selectedEntryChanged);

        this.txtNote.textProperty().addListener(this::noteTextChanged);

        ////////////////////////////////////////////////////////////////////////////////

        setColumnValueFactory(colAuthUsername, Authentication::getUsername);
        setColumnValueFactory(colAuthPassword, Authentication::getPassword);

        this.tblAuthentications.setRowFactory(tv -> new AuthenticationTableRow());
        this.tblAuthentications.getSortOrder().add(colAuthUsername);

        ////////////////////////////////////////////////////////////////////////////////

        AppPrimaryStage.getPrimaryStage().setOnCloseRequest(this::beforeClose);
        AppPrimaryStage.getPrimaryStage().addEventFilter(KeyEvent.KEY_PRESSED, this::keyEventHandler);

        mnuAutoSave.setSelected(UserConfig.getBoolean("auto_save_on_exit", false));
        mnuAutoOpen.setSelected(UserConfig.getBoolean("auto_open_on_start", false));
        mnuNoteWrap.setSelected(UserConfig.getBoolean("note_wrap_text", false));

        ///////////////////////////////////////////////

        tryAutoOpenRecentFile();
    }

    private void onEntryTableSort(SortEvent<TableView<Entry>> event) {
        List<TableColumn<Entry, ?>> sortColumns = event.getSource().getSortOrder();
        if (!sortColumns.isEmpty()) {
            TableColumn<Entry, ?> column = sortColumns.get(0);
            Category currentCategory = App.getCurrentCategory();
            currentCategory.setSortBy(getSortByName(column));
            currentCategory.setSortAscending(column.getSortType() != DESCENDING);
        }
    }

    private void tryAutoOpenRecentFile() {

        if (App.IS_DEV) {
            return;
        }

        if (UserConfig.getBoolean("auto_open_on_start", false)) {
            String filePath = UserConfig.getString("latest_file", "");
            if (StringUtils.isNotBlank(filePath)) {
                File file = new File(filePath);
                if (file.exists() && file.canRead()) {
                    openPasswordLibFile(file);
                }
            }
        }
    }

    private void keyEventHandler(KeyEvent event) {

        if (this.tblAuthentications.isFocused()) {
            if (event.isControlDown() && event.getCode() == KeyCode.X) {
                withCurrentAuthentication(auth -> ClipboardHelper.putString(auth.getUsername()));
                event.consume();
            } else if (event.isControlDown() && event.getCode() == KeyCode.C) {
                withCurrentAuthentication(auth -> ClipboardHelper.putString(auth.getPassword()));
                event.consume();
            }
        }
    }

    public void openSearch() {
        SearchDialog searchDialog = new SearchDialog();
        searchDialog.showAndWait();

        Entry entry = searchDialog.getSelectedEntry();
        if (entry != null) {
            selectEntry(entry);
        }
    }

    private void selectEntry(Entry entry) {
        tvCategories.selectCellByEntry(entry);
        tblEntries.getSelectionModel().select(entry);
        tblEntries.scrollTo(entry);
        tblEntries.requestFocus();
    }

    private void withCurrentAuthentication(Consumer<Authentication> consumer) {
        Authentication item = this.tblAuthentications.getSelectionModel().getSelectedItem();
        if (item != null) {
            consumer.accept(item);
        }
    }

    @SuppressWarnings("unused")
    private void noteTextChanged(ObservableValue<? extends String> ob, String _old, String text) {

        if (NodeUtils.getUserData(this.txtNote, "muteChange") != null) {
            return;
        }

        Entry currentEntry = App.getCurrentEntry();
        if (currentEntry != null) {
            currentEntry.setNote(text);
            App.setPasswordLibChanged();
        }
    }

    @SuppressWarnings("unused")
    private void selectedCategoryChanged(
            ObservableValue<? extends TreeItem<Category>> ob,
            TreeItem<Category> _old,
            TreeItem<Category> selected) {

        App.setCurrentCategory(selected == null ? null : selected.getValue());

        this.btnNewEntry.setDisable(selected == null);
        this.tblEntries.setDisable(selected == null);

        if (selected != null) {
            String sortBy = selected.getValue().getSortBy();
            boolean ascending = selected.getValue().isSortAscending();

            Collection<? extends TableColumn<Entry, ?>> col = getEntryTableColumn(sortBy, ascending);
            this.tblEntries.getSortOrder().setAll(col);
        }

        refreshEntryList();
    }

    private Collection<? extends TableColumn<Entry, ?>> getEntryTableColumn(String property, boolean ascending) {
        TableColumn<Entry, ?> result;

        if (property == null) {
            result = colEntryName;

        } else {
            result = this.tblEntries.getColumns().stream()
                    .filter(column -> column.getUserData().equals(property))
                    .findFirst().orElse(colEntryName);
        }

        result.setSortType(ascending? ASCENDING : DESCENDING);
        return Collections.singleton(result);
    }

    private String getSortByName(TableColumn<Entry, ?> column) {
        return String.valueOf(column.getUserData());
    }

    @SuppressWarnings("unused")
    private void selectedEntryChanged(
            ObservableValue<? extends Entry> ob,
            Entry _old,
            Entry selected
    ) {
        App.setCurrentEntry(selected);

        this.btnNewLogin.setDisable(selected == null);
        this.tblAuthentications.setDisable(selected == null);
        this.tpEntryInfo.setDisable(selected == null);
        this.txtNote.setEditable(selected != null);

        NodeUtils.setUserData(this.txtNote, "muteChange", true);
        this.txtNote.setText(selected == null ? "" : selected.getNote());
        NodeUtils.setUserData(this.txtNote, "muteChange", null);

        refreshAuthenticationList();
    }

    private void beforeClose(WindowEvent event) {

        if (App.getPasswordLib() == null) {
            return;
        }

        if (App.getPasswordLib().isChanged()) {

            if (mnuAutoSave.isSelected()) {
                trySaveOnExit(event);
                return;
            }

            ButtonType buttonType = AlertDialog.confirmYesNoCancel("保存文件",
                    "文件尚未保存。是否保存然后退出？\n\n" +
                            "点击“是”则保存文件然后退出，点击“否”则直接退出，点击“取消”不退出。");

            if (buttonType == ButtonType.CANCEL) {
                event.consume();
                return;
            }

            if (buttonType == ButtonType.YES) {
                trySaveOnExit(event);
            }
        }
    }

    private void trySaveOnExit(WindowEvent event) {
        try {
            App.getPasswordLib().save();
        } catch (Exception e) {
            logger.error("保存文件失败", e);
            AlertDialog.error("保存文件失败: ", e);
            event.consume();
        }
    }

    public void openFileClicked() {
        File file = FileDialog.showOpenFile(
                AppPrimaryStage.getPrimaryStage(), "打开密码库文件", App.FILE_EXT, App.FILE_EXT_NAME);

        if (file != null) {
            openPasswordLibFile(file);
        }
    }

    private void openPasswordLibFile(File file) {
        EnterPasswordDialog dialog = new EnterPasswordDialog(file.getName());
        ButtonType buttonType = dialog.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType == ButtonType.OK) {
            try {
                String masterPassword = dialog.getPassword();
                PasswordLib passwordLib = new PasswordLib(file, masterPassword, false);
                loadPasswordLib(passwordLib);
                App.setPasswordLib(passwordLib);
            } catch (PasswordLibException e) {
                logger.error("打开文件失败", e);
                AlertDialog.error("密码不正确", e);
            }
        }
    }

    public void newFileClicked() {
        runSafe(this::newFileClicked0);
    }

    private void newFileClicked0() {
        CreatePasswordLibDialog createPasswordLibDialog = new CreatePasswordLibDialog();
        ButtonType buttonType = createPasswordLibDialog.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType == ButtonType.OK) {

            // 先保存现有密码库
            if (App.getPasswordLib() != null) {
                App.getPasswordLib().save();
            }

            try {
                createPasswordLib(createPasswordLibDialog);
            } catch (Exception e) {
                logger.error("创建密码库失败", e);
                AlertDialog.error("创建密码库失败", e);
            }
        }
    }

    private void createPasswordLib(CreatePasswordLibDialog createPasswordLibDialog) {
        PasswordLib passwordLib = new PasswordLib(
                createPasswordLibDialog.getSaveFile(),
                createPasswordLibDialog.getMasterPassword(),
                true
        );

        passwordLib.save();
        loadPasswordLib(passwordLib);
        App.setPasswordLib(passwordLib);
    }

    private void loadPasswordLib(PasswordLib passwordLib) {
        loadCategories(passwordLib);

        this.mnuExport.setDisable(false);
        this.tvCategories.getRoot().setExpanded(true);
        this.tvCategories.getSelectionModel().select(this.tvCategories.getRoot());
        AppPrimaryStage.getPrimaryStage().setTitle(App.APP_NAME + " - " + passwordLib.filePath());

        if (!App.IS_DEV) {
            UserConfig.setString("latest_file", passwordLib.filePath());
        }
    }

    private void loadCategories(PasswordLib passwordLib) {
        Category rootCategory = passwordLib.getRootCategory();
        TreeItem<Category> root = loadCategory(rootCategory);
        this.tvCategories.setRoot(root);
    }

    private TreeItem<Category> loadCategory(Category category) {
        TreeItem<Category> treeItem = new TreeItem<>(category);
        for (Category child : category.getChildren()) {
            treeItem.getChildren().add(loadCategory(child));
        }
        treeItem.setExpanded(true);
        return treeItem;
    }

    @SuppressWarnings("UnusedReturnValue")
    private OrElse ifCategorySelectedThen(Consumer<Category> consumer) {
        TreeItem<Category> item = this.tvCategories.getSelectionModel().getSelectedItem();
        if (item == null || item.getValue() == null) {
            return OrElse.negative();
        }

        return OrElse.positive(() -> consumer.accept(item.getValue()));
    }

    private void refreshEntryList() {
        Category currentCategory = App.getCurrentCategory();
        if (currentCategory != null) {
            tblEntries.getItems().setAll(currentCategory.getEntries());
            tblEntries.sort();
        } else {
            tblEntries.getItems().clear();
        }
    }

    private void refreshAuthenticationList() {
        Entry currentEntry = App.getCurrentEntry();
        if (currentEntry != null) {
            this.tblAuthentications.getItems().setAll(currentEntry.getAuthentications());
            this.tblAuthentications.sort();
        } else {
            this.tblAuthentications.getItems().clear();
        }
    }

    public void saveClicked() {
        if (App.getPasswordLib() != null) {
            App.getPasswordLib().save();
            String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            lblStatus.setText("密码库已成功保存 [" + now + "]");
            setupClearStatus();
        }
    }

    private void setupClearStatus() {
        App.TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> lblStatus.setText(""));
            }
        }, 3000);
    }

    public void newEntryClicked() {
        EntryInfoDialog dialog = new EntryInfoDialog(null);
        ButtonType buttonType = dialog.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType == ButtonType.OK) {
            Entry entry = dialog.getEntry();
            ifCategorySelectedThen(category -> category.addEntry(entry));
            refreshEntryList();
            App.setPasswordLibChanged();
        }
    }

    public void newLoginClicked() {
        AuthenticationInfoDialog dialog = new AuthenticationInfoDialog(null);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            App.getCurrentEntry().getAuthentications().add(dialog.getAuthentication());
            refreshAuthenticationList();
        }
    }

    public void autoOpenToggleClicked() {
        UserConfig.setString("auto_open_on_start", String.valueOf(mnuAutoOpen.isSelected()));
    }

    public void autoSaveToggleClicked() {
        UserConfig.setString("auto_save_on_exit", String.valueOf(mnuAutoSave.isSelected()));
    }

    public void noteWrapToggleClicked() {
        txtNote.setWrapText(mnuNoteWrap.isSelected());
        UserConfig.setString("note_wrap_text", String.valueOf(mnuNoteWrap.isSelected()));
    }

    public void exitClicked() {
        AppPrimaryStage.getPrimaryStage().close();
    }

    public void changeMasterPasswordClicked() {
        PasswordLib passwordLib = App.getPasswordLib();
        if (passwordLib == null) {
            return;
        }

        ChangeMasterPasswordDialog dialog = new ChangeMasterPasswordDialog();
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            passwordLib.setMasterPassword(dialog.getNewPassword());
            passwordLib.save();
            AlertDialog.info("密码已修改", "密码库已经按照新的主密码重新加密保存。");
        }
    }

    public void onAuthTablePaste() {
        Authentication a = getApplicationClipboard(AUTH_CLIP_KEY);
        if (a != null) {
            Entry currentEntry = App.getCurrentEntry();
            if (currentEntry != null) {
                currentEntry.getAuthentications().add(a.clone());
                refreshAuthenticationList();
            }
        }
    }

    public void onEntryTablePaste() {
        Entry entry = getApplicationClipboard(ENTRY_CLIP_KEY);
        if (entry != null) {
            Category c = App.getCurrentCategory();
            if (c != null) {
                c.addEntry(entry.clone());
                refreshEntryList();
            }
        }
    }

    public void onAuthTableContextMenuShown() {
        mnuPasteAuthentication.setDisable(getApplicationClipboard(AUTH_CLIP_KEY) == null);
    }

    public void onEntryTableContextMenuShown() {
        mnuPasteEntry.setDisable(getApplicationClipboard(ENTRY_CLIP_KEY) == null);
    }

    public void exportFileClicked() {
        new ExportDialog().showAndWait();
    }
}
