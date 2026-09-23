package com.template.model.dao;

import java.util.List;
import com.template.model.dto.ShopItemDTO;

// contrato de persistencia do item: quem for salvar ou buscar no banco tem que honrar isso aqui
public interface IShopItemDAO {

    void createShopItem(ShopItemDTO item);

    ShopItemDTO getShopItem(int id);

    List<ShopItemDTO> getAllShopItems();

    void updateShopItem(ShopItemDTO item);

    void deleteShopItem(int id);

    // apelidos em portugues pra nao chiar se chamarem em pt-br
    default void cadastrarShopItem(ShopItemDTO item) {
        createShopItem(item);
    }

    default void atualizarShopItem(ShopItemDTO item) {
        updateShopItem(item);
    }

    default void deletarShopItem(int id) {
        deleteShopItem(id);
    }

    default List<ShopItemDTO> listarShopItems() {
        return getAllShopItems();
    }
}
