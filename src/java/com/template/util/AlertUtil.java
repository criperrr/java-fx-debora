package com.template.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utilitário para exibição de diálogos e alertas visuais padronizados na aplicação.
 */
public class AlertUtil {

    private static final String DARK_CSS = "/com/template/dark.css";

    private AlertUtil() {}

    /**
     * Exibe um alerta de erro.
     *
     * @param message Mensagem a ser exibida.
     */
    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Erro", message);
    }

    /**
     * Exibe um alerta de aviso/alerta.
     *
     * @param message Mensagem de aviso.
     */
    public static void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Aviso", message);
    }

    /**
     * Exibe uma caixa de informação.
     *
     * @param message Mensagem informativa.
     */
    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Informação", message);
    }

    /**
     * Exibe uma caixa de diálogo de confirmação com opções OK e Cancelar.
     *
     * @param title   Título da janela.
     * @param message Mensagem de confirmação.
     * @return true se o usuário confirmou (clicou em OK), false caso contrário.
     */
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
