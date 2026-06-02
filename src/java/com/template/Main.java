package com.template;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/template/loading.fxml"));
        Parent root = fxmlLoader.load();
        
        LoadingController controller = fxmlLoader.getController();

        stage.setTitle("Carregando...");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

        controller.startLoading(stage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}