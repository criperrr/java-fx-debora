package com.template.validation;

import com.template.util.FormatUtil;

// confere se o preco eh um numero de verdade e se nao eh negativo
public class PrecoValidador implements Validador<String> {

    private final String preco;

    public PrecoValidador(String preco) {
        this.preco = preco;
    }

    @Override
    public boolean validar(String valorAtual) {
        String valorParaTestar = valorAtual != null ? valorAtual : this.preco;
        if (valorParaTestar == null || valorParaTestar.trim().isEmpty()) {
            return false;
        }
        try {
            double valorNumerico = Double.parseDouble(FormatUtil.normalizePrice(valorParaTestar));
            return valorNumerico >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getMensagemErro() {
        return "Digite um preço válido (positivo ou zero)!";
    }

    @Override
    public String getValor() {
        return preco;
    }
}
