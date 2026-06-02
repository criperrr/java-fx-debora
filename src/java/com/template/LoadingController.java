package com.template;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoadingController {

    @FXML
    private Label lblStatus;

    public void startLoading(Stage stage) {
        Task<List<ShopItemDTO>> task = new Task<List<ShopItemDTO>>() {
            @Override
            protected List<ShopItemDTO> call() throws Exception {
                ShopItemDAO dao = new ShopItemDAO();
                while (true) {
                    try {
                        return dao.getAllShopItems();
                    } catch (Exception e) {
                        Platform.runLater(() -> lblStatus.setText("Falha na conexão. Retentando em 3s..."));
                        Thread.sleep(1000);
                    }
                }
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> openMainWindow(stage, task.getValue()));
        });

        new Thread(task).start();
    }

    private void openMainWindow(Stage currentStage, List<ShopItemDTO> initialData) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/template/main.fxml"));
            Parent root = fxmlLoader.load();
            
            MainController controller = fxmlLoader.getController();
            controller.setInitialData(initialData);

            Scene scene = new Scene(root, 850, 650);
            currentStage.setScene(scene);
            currentStage.centerOnScreen();
            currentStage.setTitle("Gerenciador de Itens");

            controller.postInitialize();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
