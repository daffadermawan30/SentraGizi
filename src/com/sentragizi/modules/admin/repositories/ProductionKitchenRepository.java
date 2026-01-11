package com.sentragizi.modules.admin.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.ProductionKitchen;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductionKitchenRepository {
    
    
    public List<ProductionKitchen> getAllActiveKitchens() {
        List<ProductionKitchen> list = new ArrayList<>();
        String sql = "SELECT * FROM production_kitchens WHERE is_active = 1 ORDER BY name ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            extractData(rs, list);
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    
    public List<ProductionKitchen> getAllKitchens() {
        List<ProductionKitchen> list = new ArrayList<>();
        
        String sql = "SELECT * FROM production_kitchens ORDER BY is_active DESC, name ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            extractData(rs, list);
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    
    private void extractData(ResultSet rs, List<ProductionKitchen> list) throws SQLException {
        while (rs.next()) {
            list.add(new ProductionKitchen(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("address"), 
                rs.getBoolean("is_active")
            ));
        }
    }
    
    
    public boolean save(String name, String address) {
        String sql = "INSERT INTO production_kitchens (name, address, is_active) VALUES (?, ?, 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    
    public boolean update(int id, String name, String address, boolean isActive) {
        String sql = "UPDATE production_kitchens SET name=?, address=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setInt(3, isActive ? 1 : 0);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    
    public boolean delete(int id) {
        String sql = "DELETE FROM production_kitchens WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}