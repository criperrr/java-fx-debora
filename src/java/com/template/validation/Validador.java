package com.template.validation;

// contrato basico: todo validador tem que validar, dar mensagem de erro e saber o valor que guarda
public interface Validador<T> {
    boolean validar(T valor);
    String getMensagemErro();
    T getValor();
}
