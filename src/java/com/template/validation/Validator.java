package com.template.validation;

/**
 * Interface genérica para validadores de dados.
 *
 * @param <T> Tipo do objeto ou dado a ser validado.
 */
public interface Validator<T> {
    /**
     * Valida o elemento fornecido.
     *
     * @param target Objeto ou valor a ser validado.
     * @throws ValidationException Se a validação falhar.
     */
    void validate(T target) throws ValidationException;
}
