package com.template.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utilitário para exibição de diálogos e alertas visuais padronizados na aplicação.
 */
public class AlertUtil {

    private static final String DARK_CSS = "/com/template/dark.css";
    private static final String SPONGEBOB_CSS = "/com/template/spongebob/spongebob.css";

    private AlertUtil() {}

    /**
     * Exibe um alerta de erro.
     */
    public static void showError(String message) {
        if (ThemeContext.isBobEsponja()) {
            SoundManager.play(SoundManager.FAIL);
        }
        showAlert(Alert.AlertType.ERROR, "Erro", message);
    }

    /**
     * Exibe um alerta de aviso/alerta.
     */
    public static void showWarning(String message) {
        if (ThemeContext.isBobEsponja()) {
            SoundManager.play(SoundManager.BOOWOMP);
        }
        showAlert(Alert.AlertType.WARNING, "Aviso", message);
    }

    /**
     * Exibe uma caixa de informação.
     */
    public static void showInfo(String message) {
        if (ThemeContext.isBobEsponja()) {
            SoundManager.play(SoundManager.GARY_MEOW);
        }
        showAlert(Alert.AlertType.INFORMATION, "Informação", message);
    }

    /**
     * Exibe uma caixa de diálogo de confirmação com opções OK e Cancelar.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(null);
        confirm.setContentText(message);
        applyTheme(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        applyTheme(alert);
        alert.showAndWait();
    }

    private static void applyTheme(Alert alert) {
        try {
            String cssPath = ThemeContext.isBobEsponja() ? SPONGEBOB_CSS : DARK_CSS;
            String css = AlertUtil.class.getResource(cssPath).toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (Exception ignored) {
        }
    }
}
