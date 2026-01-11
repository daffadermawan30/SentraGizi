package com.sentragizi.modules.admin.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.DistributionTarget;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistributionTargetRepository {
    
    
    public List<DistributionTarget> getAllActiveTargets() {
        List<DistributionTarget> list = new ArrayList<>();
        String sql = "SELECT * FROM distribution_targets WHERE is_active = 1 ORDER BY type, name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            extractData(rs, list);
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    
    public List<DistributionTarget> getAllTargets() {
        List<DistributionTarget> list = new ArrayList<>();
        
        String sql = "SELECT * FROM distribution_targets ORDER BY is_active DESC, type, name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            extractData(rs, list);
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    
    private void extractData(ResultSet rs, List<DistributionTarget> list) throws SQLException {
        while (rs.next()) {
            list.add(new DistributionTarget(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getString("type"),
                rs.getBoolean("is_active")
            ));
        }
    }

    

    public boolean save(String name, String location, String type) {
        String sql = "INSERT INTO distribution_targets (name, location, type, is_active) VALUES (?, ?, ?, 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setString(3, type);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean update(int id, String name, String location, String type, boolean isActive) {
        String sql = "UPDATE distribution_targets SET name=?, location=?, type=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setString(3, type);
            ps.setInt(4, isActive ? 1 : 0);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM distribution_targets WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}