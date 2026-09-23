package com.template.validation;

import java.util.ArrayList;
import java.util.List;

import com.template.util.AlertUtil;

// junta os validadores menores em fila e para no primeiro que chiar
public class ShopItemValidator implements IShopItemValidator {

    @Override
    public boolean validarItem(String nome, String preco) {
        // bota as regras que precisa checar numa lista e itera
        List<Validador<String>> validadores = new ArrayList<>();

        validadores.add(new CampoObrigatorioValidador("Nome", nome));
        validadores.add(new CampoObrigatorioValidador("Preço", preco));
        validadores.add(new PrecoValidador(preco));

        // roda cada validador; deu ruim em um, avisa na tela e cai fora
        for (Validador<String> validador : validadores) {
            if (!validador.validar(validador.getValor())) {
                AlertUtil.showWarning(validador.getMensagemErro());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean validarItem(String nome, String preco, String descricao) {
        return validarItem(nome, preco);
    }

    @Override
    public boolean validarNome(String nome) {
        Validador<String> validador = new CampoObrigatorioValidador("Nome", nome);
        if (!validador.validar(validador.getValor())) {
            AlertUtil.showWarning(validador.getMensagemErro());
            return false;
        }
        return true;
    }

    @Override
    public boolean validarPreco(String preco) {
        List<Validador<String>> validadores = new ArrayList<>();
        validadores.add(new CampoObrigatorioValidador("Preço", preco));
        validadores.add(new PrecoValidador(preco));

        for (Validador<String> validador : validadores) {
            if (!validador.validar(validador.getValor())) {
                AlertUtil.showWarning(validador.getMensagemErro());
                return false;
            }
        }
        return true;
    }
}
