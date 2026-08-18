package com.template;

import java.util.List;

public class ShopItemService {

    private final ShopItemDAO itemDAO;

    public ShopItemService() {
        this(new ShopItemDAO());
    }

    public ShopItemService(ShopItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public List<ShopItemDTO> getAllItems() {
        return itemDAO.getAllShopItems();
    }

    public void saveItem(String idStr, String name, String description, String priceStr) {
        ShopItemValidator.validate(name, priceStr);

        String normalizedPrice = FormatUtil.normalizePrice(priceStr);
        String trimmedName = name.trim();
        String trimmedDesc = description != null ? description.trim() : "";

        if (idStr != null && !idStr.trim().isEmpty()) {
            int id = Integer.parseInt(idStr.trim());
            itemDAO.updateShopItem(new ShopItemDTO(id, trimmedName, trimmedDesc, normalizedPrice));
        } else {
            itemDAO.createShopItem(new ShopItemDTO(trimmedName, trimmedDesc, normalizedPrice));
        }
    }

    public void deleteItem(int id) {
        itemDAO.deleteShopItem(id);
    }
}
