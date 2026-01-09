package com.sentragizi.modules.admin.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.ProductionKitchen;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductionKitchenRepository {
    
    // METHOD 1: Dipakai oleh PanelStage1 (Hanya yang Aktif)
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
    
    // METHOD 2: Dipakai oleh Admin (Semua Data: Aktif & Non-Aktif)
    public List<ProductionKitchen> getAllKitchens() {
        List<ProductionKitchen> list = new ArrayList<>();
        // Urutkan: Aktif dulu, baru nama
        String sql = "SELECT * FROM production_kitchens ORDER BY is_active DESC, name ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            extractData(rs, list);
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    // Helper untuk ekstrak data (Mencegah duplikasi kode)
    private void extractData(ResultSet rs, List<ProductionKitchen> list) throws SQLException {
        while (rs.next()) {
            list.add(new ProductionKitchen(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("address"), // Perbaikan: Dapur pakai 'address', bukan 'location'
                rs.getBoolean("is_active")
            ));
        }
    }
    
    // CREATE
    public boolean save(String name, String address) {
        String sql = "INSERT INTO production_kitchens (name, address, is_active) VALUES (?, ?, 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // UPDATE
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

    // DELETE
    public boolean delete(int id) {
        String sql = "DELETE FROM production_kitchens WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; // Gagal jika data sedang dipakai di tabel inspeksi
        }
    }
}