package com.hyd.pass.fxtests;

import com.hyd.fx.builders.TableViewBuilder;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.function.Function;

/**
 * @author yiding_he
 */
public class TableViewSortTest extends Application {

    private TableView<String> tableView = new TableView<>();

    {
        TableViewBuilder.of(tableView)
                .addStrColumn("value", Function.identity());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        tableView.getItems().addAll("111", "222", "333", "555", "444");
        tableView.setOnSort(event -> {
            ObservableList<TableColumn<String, ?>> sortOrder = event.getSource().getSortOrder();
            sortOrder.forEach(c -> {
                System.out.println(c.getText() + " - " + c.getSortType());
            });

            System.out.println(tableView.getItems());
        });

        primaryStage.setScene(new Scene(new BorderPane(tableView), 400, 300));
        primaryStage.show();
    }
}
