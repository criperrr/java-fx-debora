package com.template.validation;

import com.template.model.dto.ShopItemDTO;

/**
 * Validador para a entidade ShopItemDTO, orquestrando as validações de nome e preço.
 */
public class ShopItemValidator implements Validator<ShopItemDTO> {

    private static final ShopItemValidator DEFAULT_INSTANCE = new ShopItemValidator();

    private final Validator<String> nameValidator;
    private final Validator<String> priceValidator;

    public ShopItemValidator() {
        this(new RequiredFieldValidator("nome"), new PriceValidator());
    }

    public ShopItemValidator(Validator<String> nameValidator, Validator<String> priceValidator) {
        this.nameValidator = nameValidator;
        this.priceValidator = priceValidator;
    }

    @Override
    public void validate(ShopItemDTO item) throws ValidationException {
        if (item == null) {
            throw new ValidationException("item nao pode ser nulo");
        }
        validate(item.getName(), item.getPrice());
    }

    /**
     * Valida os campos individuais de um item da loja.
     *
     * @param name     Nome do item.
     * @param priceStr Representação textual do preço.
     * @throws ValidationException Caso algum dos campos seja inválido.
     */
    public void validate(String name, String priceStr) throws ValidationException {
        nameValidator.validate(name);
        priceValidator.validate(priceStr);
    }

    /**
     * Método utilitário estático para validação direta de campos.
     *
     * @param name     Nome do item.
     * @param priceStr Representação textual do preço.
     * @throws ValidationException Caso algum dos campos seja inválido.
     */
    public static void validateFields(String name, String priceStr) throws ValidationException {
        DEFAULT_INSTANCE.validate(name, priceStr);
    }
}
