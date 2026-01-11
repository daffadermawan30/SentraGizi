package com.sentragizi.modules.inspector.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.InspectionDetail;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class InspectionRepository {

    
    public long insertInspectionHeader(String batchUuid, int menuId, int userId, 
                                   int productionKitchenId, int distributionTargetId) {
        String sql = "INSERT INTO inspections (batch_uuid, menu_id, inspector_id, " +
                     "production_kitchen_id, distribution_target_id, workflow_status) " +
                     "VALUES (?, ?, ?, ?, ?, 'IN_PROGRESS')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, batchUuid);
            pstmt.setInt(2, menuId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, productionKitchenId);
            pstmt.setInt(5, distributionTargetId);
            
            int affected = pstmt.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    
    public boolean insertInspectionDetail(long inspectionId, InspectionDetail detail) {
        String sql = "INSERT INTO inspection_details (inspection_id, component_id, vendor_id, photo_path, bau_ok, rasa_ok, tekstur_ok, status_ai, status_final, follow_up_note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, inspectionId);
            ps.setInt(2, detail.getComponentId());
            ps.setInt(3, detail.getVendorId());
            ps.setString(4, detail.getPhotoPath());
            ps.setBoolean(5, detail.isBauOk());
            ps.setBoolean(6, detail.isRasaOk());
            ps.setBoolean(7, detail.isTeksturOk());
            ps.setString(8, detail.getStatusAi()); 
            ps.setString(9, detail.getStatusFinal());
            ps.setString(10, detail.getFollowUpNote()); 
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    
    public void updateBatchStatus(String uuid, String status) {
        String sql = "UPDATE inspections SET workflow_status = ? WHERE batch_uuid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    
    public java.util.List<InspectionDetail> getDetailsByBatch(String uuid) {
        java.util.List<InspectionDetail> list = new java.util.ArrayList<>();
        
        return list;
    }
    
    
    public DefaultTableModel getDetailTableForView(String uuid) {
        String[] columns = {
            "Bahan Baku", "Menu Masakan", "Vendor", "Bau", "Rasa", "Tekstur", "Hasil AI", "Status Akhir", "Foto Path", "Catatan"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        String sql = 
            "SELECT mc.raw_material_name, mc.component_name, COALESCE(v.name, '-') as vendor_name, " +
            "d.bau_ok, d.rasa_ok, d.tekstur_ok, d.status_ai, d.status_final, d.photo_path, " +
            "COALESCE(d.follow_up_note, '') as catatan " +
            "FROM inspection_details d " +
            "JOIN inspections i ON d.inspection_id = i.id " +
            "JOIN menu_components mc ON d.component_id = mc.id " +
            "LEFT JOIN vendors v ON d.vendor_id = v.id " +
            "WHERE i.batch_uuid = ? ORDER BY d.id ASC";
            
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String bau = rs.getBoolean("bau_ok") ? "✓" : "✗";
                String rasa = rs.getBoolean("rasa_ok") ? "✓" : "✗";
                String tekstur = rs.getBoolean("tekstur_ok") ? "✓" : "✗";
                model.addRow(new Object[]{
                    rs.getString("raw_material_name"), rs.getString("component_name"), rs.getString("vendor_name"),
                    bau, rasa, tekstur, rs.getString("status_ai"), rs.getString("status_final"),
                    rs.getString("photo_path"), rs.getString("catatan")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return model;
    }
        
    
    
    

    
    public ResultSet getReportData(String uuid) {
        
        String sql = "SELECT " +
                     "i.batch_uuid, i.created_at, i.workflow_status, " +
                     "m.name as menu_name, " +
                     "u.fullname as inspector_name, " +
                     
                     
                     "pk.name as kitchen_name, " +
                     "dt.name as target_name, " +
                     
                     
                     "mc.raw_material_name, " +
                     "mc.component_name, " +
                     "v.name as vendor_name, " +
                     "d.bau_ok, d.rasa_ok, d.tekstur_ok, d.status_ai, d.status_final, " +
                     "d.follow_up_note " +
                     "FROM inspection_details d " +
                     "JOIN inspections i ON d.inspection_id = i.id " +
                     "LEFT JOIN menus m ON i.menu_id = m.id " +
                     "LEFT JOIN users u ON i.inspector_id = u.id " +
                     
                     
                     "LEFT JOIN production_kitchens pk ON i.production_kitchen_id = pk.id " +
                     "LEFT JOIN distribution_targets dt ON i.distribution_target_id = dt.id " +
                     
                     
                     "LEFT JOIN menu_components mc ON d.component_id = mc.id " +
                     "LEFT JOIN vendors v ON d.vendor_id = v.id " +
                     "WHERE i.batch_uuid = ?";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uuid);
            return ps.executeQuery(); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public ResultSet getReportByDateRange(String startDate, String endDate) {
        
        String sql = "SELECT i.batch_uuid, i.created_at, m.name as menu_name, u.fullname as inspector_name, " +
                     "i.workflow_status, " +
                     
                     
                     "pk.name as kitchen_name, " +
                     "dt.name as target_name, " +
                     

                     "(SELECT COUNT(*) FROM inspection_details d WHERE d.inspection_id = i.id AND d.status_final = 'PASS') as total_pass, " +
                     "(SELECT COUNT(*) FROM inspection_details d WHERE d.inspection_id = i.id AND (d.status_final = 'FAIL' OR d.status_final = 'REJECTED')) as total_fail " +
                     "FROM inspections i " +
                     "LEFT JOIN menus m ON i.menu_id = m.id " +
                     "LEFT JOIN users u ON i.inspector_id = u.id " +
                     
                     
                     "LEFT JOIN production_kitchens pk ON i.production_kitchen_id = pk.id " +
                     "LEFT JOIN distribution_targets dt ON i.distribution_target_id = dt.id " +
                     

                     "WHERE DATE(i.created_at) BETWEEN ? AND ? " +
                     "ORDER BY i.created_at ASC";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}