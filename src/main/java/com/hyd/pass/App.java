package com.hyd.pass;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author yiding.he
 */
public class App extends Application {

    public static final String APP_NAME = "HydrogenPass";

    public static void main(String[] args) {
        Application.launch(App.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }
}
