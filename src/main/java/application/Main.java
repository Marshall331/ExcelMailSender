package application;

import application.control.Configuration;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    /**
     * Méthode principale de lancement de l'application.
     */
    public static void lancer(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Configuration c = new Configuration(new Stage());
    }
}
