package com.template.service;

import java.util.List;

import com.template.model.dao.ShopItemDAO;
import com.template.model.dto.ShopItemDTO;
import com.template.util.FormatUtil;
import com.template.validation.ShopItemValidator;

/**
 * Camada de serviço responsável por orquestrar a lógica de negócio,
 * validações e comunicação com o DAO.
 */
public class ShopItemService {

    private final ShopItemDAO itemDAO;
    private final ShopItemValidator itemValidator;

    public ShopItemService() {
        this(new ShopItemDAO(), new ShopItemValidator());
    }

    public ShopItemService(ShopItemDAO itemDAO) {
        this(itemDAO, new ShopItemValidator());
    }

    public ShopItemService(ShopItemDAO itemDAO, ShopItemValidator itemValidator) {
        this.itemDAO = itemDAO;
        this.itemValidator = itemValidator;
    }

    /**
     * Retorna todos os itens cadastrados no banco de dados.
     *
     * @return Lista com todos os ShopItemDTO.
     */
    public List<ShopItemDTO> getAllItems() {
        return itemDAO.getAllShopItems();
    }

    /**
     * Valida e salva ou atualiza um item na base de dados (formato padrão).
     */
    public void saveItem(String idStr, String name, String description, String priceStr) {
        saveItem(idStr, name, "Siri Cascudo", description, "Comum", priceStr);
    }

    /**
     * Valida e salva ou atualiza um item na base de dados (com categoria e raridade da Fenda).
     */
    public void saveItem(String idStr, String name, String category, String description, String rarity, String priceStr) {
        itemValidator.validate(name, priceStr);

        String normalizedPrice = FormatUtil.normalizePrice(priceStr);
        String trimmedName = name != null ? name.trim() : "";
        String trimmedCategory = (category != null && !category.isBlank()) ? category.trim() : "Siri Cascudo";
        String trimmedDesc = description != null ? description.trim() : "";
        String trimmedRarity = (rarity != null && !rarity.isBlank()) ? rarity.trim() : "Comum";

        if (idStr != null && !idStr.trim().isEmpty()) {
            int id = Integer.parseInt(idStr.trim());
            itemDAO.updateShopItem(new ShopItemDTO(id, trimmedName, trimmedCategory, trimmedDesc, trimmedRarity, normalizedPrice));
        } else {
            itemDAO.createShopItem(new ShopItemDTO(trimmedName, trimmedCategory, trimmedDesc, trimmedRarity, normalizedPrice));
        }
    }

    /**
     * Remove um item da base de dados pelo seu ID.
     *
     * @param id Identificador do item a ser excluído.
     */
    public void deleteItem(int id) {
        itemDAO.deleteShopItem(id);
    }
}
