package com.hyd.pass.controllers;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.FileDialog;
import com.hyd.pass.App;
import com.hyd.pass.Logger;
import com.hyd.pass.dialogs.CreatePasswordDialog;
import com.hyd.pass.dialogs.EnterPasswordDialog;
import com.hyd.pass.dialogs.EntryInfoDialog;
import com.hyd.pass.fx.EntryTableRow;
import com.hyd.pass.model.*;
import com.hyd.pass.utils.OrElse;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
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

    public TableView<Authentication> tvAuthentications;

    public Button btnNewEntry;

    public Button btnNewLogin;

    public TextArea txtNote;

    public TabPane tpEntryInfo;

    public TableColumn<Entry, String> colEntryName;

    public TableColumn<Entry, String> colEntryLocation;

    public TableColumn<Entry, String> colEntryComment;

    public void initialize() {
        this.split1.setDividerPositions(0.2);
        this.split2.setDividerPositions(0.4);
        this.tvCategories.getSelectionModel().selectedItemProperty().addListener(this::selectedCategoryChanged);

        setColumnValueFactory(colEntryName, Entry::getName);
        setColumnValueFactory(colEntryLocation, Entry::getLocation);
        setColumnValueFactory(colEntryComment, Entry::getComment);

        this.tblEntries.setRowFactory(tv -> new EntryTableRow());

        AppPrimaryStage.getPrimaryStage().setOnCloseRequest(this::beforeClose);
    }

    private void selectedCategoryChanged(
            ObservableValue<? extends TreeItem<Category>> ob,
            TreeItem<Category> _old,
            TreeItem<Category> selected) {

        this.btnNewEntry.setDisable(selected == null);
        this.tblEntries.setDisable(selected == null);

        refreshEntryList();
    }

    private void beforeClose(WindowEvent event) {

        if (App.getPasswordLib() == null) {
            return;
        }

        if (App.getPasswordLib().isChanged()) {
            ButtonType buttonType = AlertDialog.confirmYesNoCancel("保存文件",
                    "文件尚未保存。是否保存然后退出？\n\n" +
                            "点击“是”则保存文件然后退出，点击“否”则直接退出，点击“取消”不退出。");

            if (buttonType == ButtonType.CANCEL) {
                event.consume();
                return;
            }

            if (buttonType == ButtonType.YES) {
                try {
                    App.getPasswordLib().save();
                } catch (Exception e) {
                    logger.error("保存文件失败", e);
                    AlertDialog.error("保存文件失败: ", e);
                    event.consume();
                }
            }
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
                    PasswordLib passwordLib = new PasswordLib(file, dialog.getPassword(), false);
                    loadPasswordLib(passwordLib);
                    App.setPasswordLib(passwordLib);
                } catch (PasswordLibException e) {
                    AlertDialog.error("密码不正确");
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
            try {
                createPasswordLib(createPasswordDialog);
            } catch (Exception e) {
                logger.error("创建密码库失败", e);
                AlertDialog.error("创建密码库失败");
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
        if (item == null) {
            return new OrElse(false);
        }

        Category category = item.getValue();
        if (category == null) {
            return new OrElse(false);
        }

        return new OrElse(true, () -> consumer.accept(category));
    }

    private void refreshEntryList() {
        ifCategorySelectedThen(
                category -> tblEntries.getItems().setAll(category.getEntries())
        ).orElse(
                () -> tblEntries.getItems().clear()
        );
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
    }
}
