package application.control;

import application.Main;
import application.view.SendMailsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SendMails {

    public SendMails(Stage _primaryStage) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("view/SendMails.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            _primaryStage.setScene(scene);
            _primaryStage.setTitle("Envoi en cours...");
            _primaryStage.setResizable(false);

            SendMailsController dbmfcViewController = loader.getController();
            dbmfcViewController.initContext(_primaryStage, this);

            dbmfcViewController.displayDialog();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
