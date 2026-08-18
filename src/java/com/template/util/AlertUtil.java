package com.template.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {

    private static final String DARK_CSS = "/com/template/dark.css";

    private AlertUtil() {}

    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "erro", message);
    }

    public static void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "aviso", message);
    }

    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "informação", message);
    }

    public static boolean showConfirmation(String title, String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(null);
        confirm.setContentText(message);
        applyDarkTheme(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        applyDarkTheme(alert);
        alert.showAndWait();
    }

    private static void applyDarkTheme(Alert alert) {
        try {
            String css = AlertUtil.class.getResource(DARK_CSS).toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (Exception ignored) {
        }
    }
}
