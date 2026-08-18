package com.template.validation;

/**
 * Exceção lançada quando ocorre uma falha em alguma regra de validação de dados.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
