package com.sentragizi.modules.inspector.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class InspectionRepository {

    // 1. Ambil Antrean Masak (Hanya yang status DRAFT atau COOKING)
    public DefaultTableModel getQueueTable() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"UUID", "Waktu", "Menu", "Status"}, 0);
        String sql = "SELECT i.batch_uuid, i.created_at, m.name, i.workflow_status " +
                     "FROM inspections i JOIN menus m ON i.menu_id = m.id " +
                     "WHERE i.workflow_status IN ('DRAFT', 'COOKING') " +
                     "ORDER BY i.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("batch_uuid"),
                    rs.getString("created_at"),
                    rs.getString("name"),
                    rs.getString("workflow_status")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return model;
    }

    // 2. AMBIL CHECKLIST (Fitur Utama Ide Kamu)
    // Mengambil nama bahan yang 'needs_raw_check = 1' saja
    public List<String> getRawChecklist(int menuId) {
        List<String> items = new ArrayList<>();
        String sql = "SELECT raw_material_name FROM menu_components WHERE menu_id = ? AND needs_raw_check = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                items.add(rs.getString("raw_material_name"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return items;
    }

    // 3. Simpan Tahap 1 (Start Batch)
    public boolean startBatch(String uuid, int menuId, int inspectorId, int vendorId, String photoPath, String status, String notes) {
        // Status: Jika PASS -> 'COOKING', Jika FAIL -> 'REJECTED_RAW'
        String wfStatus = status.equals("PASS") ? "COOKING" : "REJECTED_RAW";
        
        String sql = "INSERT INTO inspections (batch_uuid, menu_id, inspector_id, vendor_id, raw_photo_path, stage1_status, stage1_notes, workflow_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setInt(2, menuId);
            ps.setInt(3, inspectorId);
            ps.setInt(4, vendorId);
            ps.setString(5, photoPath);
            ps.setString(6, status); // PASS/FAIL
            ps.setString(7, notes);
            ps.setString(8, wfStatus);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    // 4. AMBIL DAFTAR KOMPONEN MENU (Untuk dibandingkan)
    public List<String> getAllComponents(int menuId) {
        List<String> items = new ArrayList<>();
        String sql = "SELECT component_name FROM menu_components WHERE menu_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                items.add(rs.getString("component_name"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return items;
    }

    // 5. HELPER: AMBIL MENU ID DARI UUID BATCH
    public int getMenuIdByUUID(String uuid) {
        String sql = "SELECT menu_id FROM inspections WHERE batch_uuid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getInt("menu_id");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // 6. SIMPAN FINAL (Update Data Tahap 2)
    public boolean saveFinal(String uuid, String koki, double suhu, int berat, String photo, String jsonAI, String status) {
        String sql = "UPDATE inspections SET chef_name=?, measured_temp=?, measured_weight=?, stage2_photo_path=?, ai_counting_result=?, workflow_status=?, sync_status='PENDING' WHERE batch_uuid=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, koki);
            ps.setDouble(2, suhu);
            ps.setInt(3, berat);
            ps.setString(4, photo);
            ps.setString(5, jsonAI);
            ps.setString(6, status); // 'COMPLETED' atau 'REJECTED_FINAL'
            ps.setString(7, uuid);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}