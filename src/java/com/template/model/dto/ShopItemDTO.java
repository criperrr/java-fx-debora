package com.template.model.dto;

/**
 * Objeto de Transferência de Dados (DTO) representando um item da loja / Fenda do Biquíni.
 */
public class ShopItemDTO {
    private int id;
    private String name;
    private String category = "Siri Cascudo";
    private String description;
    private String rarity = "Comum";
    private String price;

    public ShopItemDTO(String name, String description, String price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public ShopItemDTO(int id, String name, String description, String price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public ShopItemDTO(int id, String name, String category, String description, String rarity, String price) {
        this.id = id;
        this.name = name;
        this.category = (category != null && !category.isBlank()) ? category : "Siri Cascudo";
        this.description = description;
        this.rarity = (rarity != null && !rarity.isBlank()) ? rarity : "Comum";
        this.price = price;
    }

    public ShopItemDTO(String name, String category, String description, String rarity, String price) {
        this.name = name;
        this.category = (category != null && !category.isBlank()) ? category : "Siri Cascudo";
        this.description = description;
        this.rarity = (rarity != null && !rarity.isBlank()) ? rarity : "Comum";
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
