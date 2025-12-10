package com.sentragizi.modules.admin.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

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
}