package com.template;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principal responsável por inicializar a aplicação JavaFX.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/template/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 850, 650);
        scene.getStylesheets().add(getClass().getResource("/com/template/dark.css").toExternalForm());

        stage.setTitle("Gerenciador de Itens");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}