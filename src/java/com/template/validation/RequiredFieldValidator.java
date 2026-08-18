package com.template.validation;

/**
 * Validador para garantir o preenchimento de campos obrigatórios (não nulos e não vazios).
 */
public class RequiredFieldValidator implements Validator<String> {

    private final String fieldName;

    public RequiredFieldValidator() {
        this("campo");
    }

    public RequiredFieldValidator(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " e obrigatorio");
        }
    }

    public String getFieldName() {
        return fieldName;
    }
}
