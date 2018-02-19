package com.hyd.pass.controllers;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.FileDialog;
import com.hyd.fx.system.ClipboardHelper;
import com.hyd.pass.App;
import com.hyd.pass.Logger;
import com.hyd.pass.conf.UserConfig;
import com.hyd.pass.dialogs.*;
import com.hyd.pass.fx.AuthenticationTableRow;
import com.hyd.pass.fx.EntryTableRow;
import com.hyd.pass.model.*;
import com.hyd.pass.utils.OrElse;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.function.Consumer;

import static com.hyd.fx.cells.TableViewHelper.setColumnValueFactory;

/**
 * @author yiding.he
 */
public class MainController extends BaseController {

    private static final Logger logger = Logger.getLogger(MainController.class);

    public SplitPane split1;

    public SplitPane split2;

    public TreeView<Category> tvCategories;

    public TableView<Entry> tblEntries;

    public TableView<Authentication> tblAuthentications;

    public Button btnNewEntry;

    public Button btnNewLogin;

    public TextArea txtNote;

    public TabPane tpEntryInfo;

    public TableColumn<Entry, String> colEntryName;

    public TableColumn<Entry, String> colEntryLocation;

    public TableColumn<Entry, String> colEntryComment;

    public TableColumn<Authentication, String> colAuthUsername;

    public TableColumn<Authentication, String> colAuthPassword;

    public CheckMenuItem mnuAutoSave;

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
        setColumnValueFactory(colEntryComment, Entry::getComment);

        this.tblEntries.setRowFactory(tv -> new EntryTableRow());
        this.tblEntries.getSortOrder().add(colEntryName);

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
        mnuAutoSave.setSelected(Boolean.parseBoolean(UserConfig.getString("auto_save_on_exit", "false")));

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

    private void withCurrentAuthentication(Consumer<Authentication> consumer) {
        Authentication item = this.tblAuthentications.getSelectionModel().getSelectedItem();
        if (item != null) {
            consumer.accept(item);
        }
    }

    private void noteTextChanged(ObservableValue<? extends String> ob, String _old, String text) {
        Entry currentEntry = App.getCurrentEntry();
        if (currentEntry != null) {
            currentEntry.setNote(text);
            App.setPasswordLibChanged();
        }
    }

    private void selectedCategoryChanged(
            ObservableValue<? extends TreeItem<Category>> ob,
            TreeItem<Category> _old,
            TreeItem<Category> selected) {

        App.setCurrentCategory(selected == null ? null : selected.getValue());

        this.btnNewEntry.setDisable(selected == null);
        this.tblEntries.setDisable(selected == null);

        refreshEntryList();
    }

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
        this.txtNote.setText(selected == null ? "" : selected.getNote());

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
            EnterPasswordDialog dialog = new EnterPasswordDialog();
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
    }

    public void newFileClicked() {
        runSafe(this::newFileClicked0);
    }

    private void newFileClicked0() throws Exception {
        CreatePasswordDialog createPasswordDialog = new CreatePasswordDialog();
        ButtonType buttonType = createPasswordDialog.showAndWait().orElse(ButtonType.CANCEL);

        if (buttonType == ButtonType.OK) {

            // 先保存现有密码库
            if (App.getPasswordLib() != null) {
                App.getPasswordLib().save();
            }

            try {
                createPasswordLib(createPasswordDialog);
            } catch (Exception e) {
                logger.error("创建密码库失败", e);
                AlertDialog.error("创建密码库失败", e);
            }
        }
    }

    private void createPasswordLib(CreatePasswordDialog createPasswordDialog) {
        PasswordLib passwordLib = new PasswordLib(
                createPasswordDialog.getSaveFile(),
                createPasswordDialog.getMasterPassword(),
                true
        );

        passwordLib.save();
        loadPasswordLib(passwordLib);
        App.setPasswordLib(passwordLib);
    }

    private void loadPasswordLib(PasswordLib passwordLib) {
        loadCategories(passwordLib);
        this.tvCategories.getRoot().setExpanded(true);
        this.tvCategories.getSelectionModel().select(this.tvCategories.getRoot());
        AppPrimaryStage.getPrimaryStage().setTitle(App.APP_NAME + " - " + passwordLib.filePath());
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
        return treeItem;
    }

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
            AlertDialog.info("保存完毕", "密码库已成功保存。");
        }
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

    public void autoSaveToggleClicked() {
        UserConfig.setString("auto_save_on_exit", String.valueOf(mnuAutoSave.isSelected()));
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
            App.setMasterPassword(dialog.getNewPassword());
            passwordLib.save();
            AlertDialog.info("密码已修改", "密码库已经按照新的主密码重新加密保存。");
        }
    }
}
