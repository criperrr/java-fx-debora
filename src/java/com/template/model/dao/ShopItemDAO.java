package com.template.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.template.model.dto.ShopItemDTO;

public class ShopItemDAO {

    public void createShopItem(ShopItemDTO item) {
        String sql = "INSERT INTO shopItems (name, description, price) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getPrice());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir item na base de dados: ", e);
        }
    }

    public ShopItemDTO getShopItem(int id) {
        String sql = "SELECT id, name, description, price FROM shopItems WHERE id = ?";
        ShopItemDTO item = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    item = new ShopItemDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("price")
                    );
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item na base de dados: ", e);
        }
        
        return item;
    }

    public List<ShopItemDTO> getAllShopItems() {
        String sql = "SELECT id, name, description, price FROM shopItems";
        List<ShopItemDTO> items = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ShopItemDTO item = new ShopItemDTO(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("price")
                );
                items.add(item);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens da base de dados: ", e);
        }
        
        return items;
    }

    public void updateShopItem(ShopItemDTO item) {
        String sql = "UPDATE shopItems SET name = ?, description = ?, price = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getPrice());
            stmt.setInt(4, item.getId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item na base de dados: ", e);
        }
    }

    public void deleteShopItem(int id) {
        String sql = "DELETE FROM shopItems WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar item na base de dados: ", e);
        }
    }
}
