package com.template;

public class FormatUtil {

    private FormatUtil() {}

    public static String normalizePrice(String price) {
        if (price == null) {
            return "";
        }
        return price.trim().replace(",", ".");
    }

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
