package com.template.util;

/**
 * Utilitário para normalização e formatação de valores.
 */
public class FormatUtil {

    private FormatUtil() {}

    /**
     * Normaliza a string de preço, substituindo vírgula por ponto.
     *
     * @param price Texto original do preço.
     * @return String normalizada com ponto como separador decimal.
     */
    public static String normalizePrice(String price) {
        if (price == null) {
            return "";
        }
        return price.trim().replace(",", ".");
    }

    /**
     * Formata um valor numérico textual para o padrão monetário brasileiro (R$ 0,00).
     *
     * @param price Texto do preço a ser formatado.
     * @return String formatada como moeda ou o próprio valor se não for numérico.
     */
    public static String formatCurrency(String price) {
        if (price == null || price.trim().isEmpty()) {
            return null;
        }
        try {
            double val = Double.parseDouble(normalizePrice(price));
            return String.format("R$ %.2f", val);
        } catch (NumberFormatException e) {
            return price;
        }
    }
}
