package com.sentragizi.modules.admin.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.Vendor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendorRepository {
    
    
    public boolean saveVendor(String name, String specialty) {
        String sql = "INSERT INTO vendors (name, specialty) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, specialty); 
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    
    public List<Vendor> getAllVendors() {
        List<Vendor> list = new ArrayList<>();
        String sql = "SELECT * FROM vendors ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while(rs.next()) {
                list.add(new Vendor(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("specialty")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    
    public boolean updateVendor(int id, String name, String specialty) {
        String sql = "UPDATE vendors SET name=?, specialty=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, specialty);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    
    public boolean deleteVendor(int id) {
        String sql = "DELETE FROM vendors WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}