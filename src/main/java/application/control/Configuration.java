package application.control;

import application.Main;
import application.view.ConfigurationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Configuration{

    public Configuration(Stage primaryStage) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("view/Configuration.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("Configuration");
            primaryStage.setResizable(false);

            ConfigurationController dbmfcViewController = loader.getController();
            dbmfcViewController.initContext(primaryStage, this);
            scene.getStylesheets().add("application.css");

            dbmfcViewController.displayDialog();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
