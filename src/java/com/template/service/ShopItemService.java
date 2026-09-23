package com.template.service;

import java.util.List;

import com.template.model.dao.IShopItemDAO;
import com.template.model.dao.ShopItemDAO;
import com.template.model.dto.ShopItemDTO;
import com.template.util.FormatUtil;

// orquestra as regras do item e delega o banco pro dao
public class ShopItemService implements IShopItemService {

    private final IShopItemDAO itemDAO;

    public ShopItemService() {
        this(new ShopItemDAO());
    }

    public ShopItemService(IShopItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public void cadastrarItem(ShopItemDTO item) {
        itemDAO.createShopItem(item);
    }

    @Override
    public void atualizarItem(ShopItemDTO item) {
        itemDAO.updateShopItem(item);
    }

    @Override
    public void deletarItem(int id) {
        itemDAO.deleteShopItem(id);
    }

    @Override
    public List<ShopItemDTO> listarItens() {
        return itemDAO.getAllShopItems();
    }

    @Override
    public List<ShopItemDTO> getAllItems() {
        return listarItens();
    }

    @Override
    public void saveItem(String idStr, String name, String description, String priceStr) {
        String normalizedPrice = FormatUtil.normalizePrice(priceStr);
        String trimmedName = name != null ? name.trim() : "";
        String trimmedDesc = description != null ? description.trim() : "";

        if (idStr != null && !idStr.trim().isEmpty()) {
            int id = Integer.parseInt(idStr.trim());
            atualizarItem(new ShopItemDTO(id, trimmedName, trimmedDesc, normalizedPrice));
        } else {
            cadastrarItem(new ShopItemDTO(trimmedName, trimmedDesc, normalizedPrice));
        }
    }

    @Override
    public void deleteItem(int id) {
        deletarItem(id);
    }
}
