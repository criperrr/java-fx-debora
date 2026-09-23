package com.template.util;

// fachada pros alertas visuais pra nao espalhar codigo de tela por todo canto
public class DialogUtil {

    private DialogUtil() {}

    public static void showWarning(String message) {
        AlertUtil.showWarning(message);
    }

    public static void showError(String message) {
        AlertUtil.showError(message);
    }

    public static void showInfo(String message) {
        AlertUtil.showInfo(message);
    }

    public static void showInformation(String message) {
        AlertUtil.showInformation(message);
    }

    public static boolean showConfirmation(String title, String message) {
        return AlertUtil.showConfirmation(title, message);
    }
}
