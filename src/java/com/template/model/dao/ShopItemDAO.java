package com.template.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.template.model.dto.ShopItemDTO;
import com.template.util.ThemeContext;

/**
 * Objeto de acesso a dados (DAO) para operações de persistência da entidade ShopItem.
 */
public class ShopItemDAO {

    private String getTableName() {
        return ThemeContext.isBobEsponja() ? "spongebob_items" : "shopItems";
    }

    /**
     * Insere um novo item na base de dados.
     *
     * @param item Dados do item a ser cadastrado.
     */
    public void createShopItem(ShopItemDTO item) {
        String sql;
        if (ThemeContext.isBobEsponja()) {
            sql = "INSERT INTO spongebob_items (name, category, description, rarity, price) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO shopItems (name, description, price) VALUES (?, ?, ?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (ThemeContext.isBobEsponja()) {
                stmt.setString(1, item.getName());
                stmt.setString(2, item.getCategory());
                stmt.setString(3, item.getDescription());
                stmt.setString(4, item.getRarity());
                stmt.setString(5, item.getPrice());
            } else {
                stmt.setString(1, item.getName());
                stmt.setString(2, item.getDescription());
                stmt.setString(3, item.getPrice());
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir item na base de dados: ", e);
        }
    }

    /**
     * Busca um item específico pelo seu ID.
     *
     * @param id Identificador do item.
     * @return ShopItemDTO correspondente ou null caso não encontrado.
     */
    public ShopItemDTO getShopItem(int id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        ShopItemDTO item = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (ThemeContext.isBobEsponja()) {
                        item = new ShopItemDTO(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getString("rarity"),
                            rs.getString("price")
                        );
                    } else {
                        item = new ShopItemDTO(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("price")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item na base de dados: ", e);
        }

        return item;
    }

    /**
     * Lista todos os itens cadastrados na tabela.
     *
     * @return Lista contendo os itens cadastrados.
     */
    public List<ShopItemDTO> getAllShopItems() {
        String sql = "SELECT * FROM " + getTableName() + " ORDER BY id ASC";
        List<ShopItemDTO> items = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ShopItemDTO item;
                if (ThemeContext.isBobEsponja()) {
                    item = new ShopItemDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("rarity"),
                        rs.getString("price")
                    );
                } else {
                    item = new ShopItemDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("price")
                    );
                }
                items.add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens da base de dados: ", e);
        }

        return items;
    }

    /**
     * Atualiza os dados de um item existente.
     *
     * @param item Item com os novos dados a serem salvos.
     */
    public void updateShopItem(ShopItemDTO item) {
        String sql;
        if (ThemeContext.isBobEsponja()) {
            sql = "UPDATE spongebob_items SET name = ?, category = ?, description = ?, rarity = ?, price = ? WHERE id = ?";
        } else {
            sql = "UPDATE shopItems SET name = ?, description = ?, price = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (ThemeContext.isBobEsponja()) {
                stmt.setString(1, item.getName());
                stmt.setString(2, item.getCategory());
                stmt.setString(3, item.getDescription());
                stmt.setString(4, item.getRarity());
                stmt.setString(5, item.getPrice());
                stmt.setInt(6, item.getId());
            } else {
                stmt.setString(1, item.getName());
                stmt.setString(2, item.getDescription());
                stmt.setString(3, item.getPrice());
                stmt.setInt(4, item.getId());
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item na base de dados: ", e);
        }
    }

    /**
     * Exclui um item da base de dados pelo seu identificador.
     *
     * @param id ID do item a ser excluído.
     */
    public void deleteShopItem(int id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar item na base de dados: ", e);
        }
    }
}
