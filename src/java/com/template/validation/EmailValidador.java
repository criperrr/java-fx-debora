package com.template.validation;

import java.util.regex.Pattern;

// regex simples pra checar se tem formato de email sem inventar moda
public class EmailValidador implements Validador<String> {
    private static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.\\w+$";
    private final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    private final String email;

    public EmailValidador(String email) {
        this.email = email;
    }

    @Override
    public boolean validar(String valorAtual) {
        return this.email != null && pattern.matcher(this.email).matches();
    }

    @Override
    public String getMensagemErro() {
        return "Digite um e-mail válido (exemplo@dominio.com)!";
    }

    @Override
    public String getValor() {
        return email;
    }
}
