package com.template.validation;

import com.template.util.FormatUtil;

public class ShopItemValidator {

    private ShopItemValidator() {}

    public static void validate(String name, String priceStr) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("nome e obrigatorio");
        }
        if (priceStr == null || priceStr.trim().isEmpty()) {
            throw new ValidationException("preco e obrigatorio");
        }
        try {
            double price = Double.parseDouble(FormatUtil.normalizePrice(priceStr));
            if (price < 0) {
                throw new ValidationException("preco nao pode ser negativo");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("preco invalido - use ponto ou virgula como separador decimal");
        }
    }
}
