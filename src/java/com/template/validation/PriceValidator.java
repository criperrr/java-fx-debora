package com.template.validation;

import com.template.util.FormatUtil;

/**
 * Validador responsável pela validação do formato e regras de preço.
 */
public class PriceValidator implements Validator<String> {

    private final RequiredFieldValidator requiredValidator = new RequiredFieldValidator("preco");

    @Override
    public void validate(String priceStr) throws ValidationException {
        requiredValidator.validate(priceStr);

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
