package com.hyd.pass;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.dialog.FileDialog;
import com.hyd.pass.fx.Icons;
import com.hyd.pass.model.Category;
import com.hyd.pass.model.Entry;
import com.hyd.pass.model.PasswordLib;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Timer;

/**
 * JVM parameters need to be added at runtime:
 * <pre>
 *     --module-path [local path javafx-sdk-20.0.1\lib]
 *     --add-modules javafx.controls
 *     --add-exports javafx.base/com.sun.javafx.binding=ALL-UNNAMED
 *     --add-exports javafx.base/com.sun.javafx.reflect=ALL-UNNAMED
 *     --add-exports javafx.base/com.sun.javafx.beans=ALL-UNNAMED
 *     --add-exports javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
 *     --add-exports javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED
 *     --add-exports javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED
 *     --add-exports javafx.graphics/com.sun.javafx.util=ALL-UNNAMED
 * </pre>
 */
@SpringBootApplication
public class App extends Application {

    public static final String APP_NAME = "Hydrogen Pass 密码管理";

    public static final String FILE_EXT = "*.hpass";

    public static final String FILE_EXT_NAME = "HPass 密码库";

    public static final boolean IS_DEV = Boolean.parseBoolean(System.getProperty("dev", "false"));

    public static final Timer TIMER = new Timer(true);

    private static PasswordLib passwordLib;

    public static void setPasswordLib(PasswordLib passwordLib) {
        App.passwordLib = passwordLib;
    }

    public static PasswordLib getPasswordLib() {
        return passwordLib;
    }

    public static void setPasswordLibChanged() {
        if (passwordLib != null) {
            passwordLib.setChanged(true);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        Application.launch(App.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppPrimaryStage.setPrimaryStage(primaryStage);
        FileDialog.setInitDirectory(new File("."));

        Parent parent = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.getIcons().add(Icons.Logo.getImage());
        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

    ////////////////////////////////////////////////////////////////////////////////

    private static Category currentCategory;

    private static Entry currentEntry;

    public static Category getCurrentCategory() {
        return currentCategory;
    }

    public static void setCurrentCategory(Category currentCategory) {
        App.currentCategory = currentCategory;
    }

    public static Entry getCurrentEntry() {
        return currentEntry;
    }

    public static void setCurrentEntry(Entry currentEntry) {
        App.currentEntry = currentEntry;
    }
}
