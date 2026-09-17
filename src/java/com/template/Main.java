package com.template;

import java.util.Arrays;

import com.template.model.dao.DatabaseConnection;
import com.template.util.SoundManager;
import com.template.util.ThemeContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principal responsável por inicializar a aplicação JavaFX.
 */
public class Main extends Application {

    private static String[] launchArgs = new String[0];

    @Override
    public void init() {
        var params = getParameters();
        if (params != null) {
            boolean hasBobParam = params.getRaw().contains("--bob-esponja") ||
                                  "true".equalsIgnoreCase(System.getProperty("bob.esponja"));
            if (hasBobParam) {
                ThemeContext.setBobEsponja(true);
            }
        }
        if (Arrays.asList(launchArgs).contains("--bob-esponja")) {
            ThemeContext.setBobEsponja(true);
        }
        // Inicializa as tabelas adequadas ao modo ativo
        DatabaseConnection.initTables();
    }

    @Override
    public void start(Stage stage) throws Exception {
        if (ThemeContext.isBobEsponja()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/template/spongebob/main_spongebob.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1040, 820);
            scene.getStylesheets().add(getClass().getResource("/com/template/spongebob/spongebob.css").toExternalForm());

            stage.setTitle("🍍 Siri Cascudo 2000 - Caixa da Fenda do Biquíni");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            SoundManager.play(SoundManager.GARY_MEOW);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/template/main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 850, 650);
            scene.getStylesheets().add(getClass().getResource("/com/template/dark.css").toExternalForm());

            stage.setTitle("Gerenciador de Itens");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launchArgs = args;
        for (String arg : args) {
            if ("--bob-esponja".equalsIgnoreCase(arg)) {
                ThemeContext.setBobEsponja(true);
            }
        }
        launch(args);
    }
}