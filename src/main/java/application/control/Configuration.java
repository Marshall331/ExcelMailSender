package application.control;

import application.view.ConfigurationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Configuration extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Configuration.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("Configuration");
            primaryStage.setResizable(false);

            ConfigurationController controller = loader.getController();
            controller.initContext(primaryStage, this);
            controller.displayDialog();

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void runApp(String[] args) {
        launch(args);
    }
}
