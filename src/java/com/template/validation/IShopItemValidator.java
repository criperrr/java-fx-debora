package com.template.validation;

// abstrai as validacoes do item pra nao amarrar quem chama na classe concreta
public interface IShopItemValidator {

    boolean validarItem(String nome, String preco);

    boolean validarItem(String nome, String preco, String descricao);

    boolean validarNome(String nome);

    boolean validarPreco(String preco);
}
