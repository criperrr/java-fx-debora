package com.template;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.template.controller.MainController;
import com.template.model.dao.IShopItemDAO;
import com.template.model.dao.ShopItemDAO;
import com.template.service.IShopItemService;
import com.template.service.ShopItemService;
import com.template.validation.IShopItemValidator;
import com.template.validation.ShopItemValidator;

// ponto de entrada da aplicacao: so sobe a interface e amarra as dependencias sem complicar
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // instancia quem realmente faz o trabalho pesado pra plugar nas interfaces
        IShopItemDAO itemDAO = new ShopItemDAO();
        IShopItemService itemService = new ShopItemService(itemDAO);
        IShopItemValidator itemValidator = new ShopItemValidator();

        // o fxmlloader e meio burro com construtor parametrizado, entao a fabrica resolve isso
        FXMLLoader loader = new FXMLLoader();
        URL fxmlLocation = getClass().getResource("/com/template/main.fxml");
        if (fxmlLocation == null) {
            System.err.println("erro: main.fxml nao encontrado.");
            return;
        }
        loader.setLocation(fxmlLocation);

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == MainController.class) {
                // devolve o controller montado com o servico e o validador injetados
                return new MainController(itemService, itemValidator);
            }
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent root = loader.load();

        Scene scene = new Scene(root, 850, 650);
        scene.getStylesheets().add(getClass().getResource("/com/template/dark.css").toExternalForm());

        primaryStage.setTitle("Gerenciador de Itens");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}