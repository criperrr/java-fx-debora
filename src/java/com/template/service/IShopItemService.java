package com.template.service;

import java.util.List;
import com.template.model.dto.ShopItemDTO;

// isola as regras do negocio dos itens pra que ninguem dependa direto de classe concreta
public interface IShopItemService {

    void cadastrarItem(ShopItemDTO item);

    void atualizarItem(ShopItemDTO item);

    void deletarItem(int id);

    List<ShopItemDTO> listarItens();

    List<ShopItemDTO> getAllItems();

    void saveItem(String idStr, String name, String description, String priceStr);

    void deleteItem(int id);
}
