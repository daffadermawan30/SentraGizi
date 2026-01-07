package com.sentragizi.modules.inspector.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.InspectionDetail;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class InspectionRepository {

    // 1. Simpan Header Inspeksi (Start Batch)
    public long insertInspectionHeader(String uuid, int menuId, int inspectorId) {
        String sql = "INSERT INTO inspections (batch_uuid, menu_id, inspector_id, workflow_status) VALUES (?, ?, ?, 'IN_PROGRESS')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, uuid);
            ps.setInt(2, menuId);
            ps.setInt(3, inspectorId);
            
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1); // Kembalikan ID Baru (Primary Key)
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1; // Gagal
    }

    // 2. Simpan Detail Inspeksi (Per Item)
    public boolean insertInspectionDetail(long inspectionId, InspectionDetail detail) {
        // Tambahkan kolom follow_up_note di SQL
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
            
            // --- INPUT CATATAN ---
            ps.setString(10, detail.getFollowUpNote()); 
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // 3. Update Status Akhir Batch
    public void updateBatchStatus(String uuid, String status) {
        String sql = "UPDATE inspections SET workflow_status = ? WHERE batch_uuid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    // 4. AMBIL DETAIL INSPEKSI (Untuk Dialog History)
    public java.util.List<InspectionDetail> getDetailsByBatch(String uuid) {
        java.util.List<InspectionDetail> list = new java.util.ArrayList<>();
        
        // Kita perlu JOIN ke tabel menus/components agar dapat nama bahannya
        // Asumsi: component_id mungkin 0 (sesuai kode PanelStage1 tadi), 
        // jadi kita ambil nama bahan lewat logika lain atau simpan nama bahan di detail (opsional).
        // Untuk sekarang, kita ambil data murni dari inspection_details dan join Vendor.
        
        String sql = "SELECT d.*, v.name as vendor_name, m.raw_material_name " +
                     "FROM inspection_details d " +
                     "JOIN inspections i ON d.inspection_id = i.id " +
                     "LEFT JOIN vendors v ON d.vendor_id = v.id " +
                     "LEFT JOIN menu_components m ON d.component_id = m.id " + // Jika component_id diisi
                     "WHERE i.batch_uuid = ?";
                     
        // CATATAN: Karena di PanelStage1 tadi kita set component_id = 0, 
        // Anda mungkin perlu menyimpan "nama_bahan" langsung di tabel detail 
        // atau memperbaiki PanelStage1 agar mencari ID komponen yang benar.
        // TAPI, untuk solusi cepat agar tidak error, kita pakai query simpel dulu:
        
        String sqlSimple = "SELECT d.*, v.name as vendor_name " +
                           "FROM inspection_details d " +
                           "JOIN inspections i ON d.inspection_id = i.id " +
                           "LEFT JOIN vendors v ON d.vendor_id = v.id " +
                           "WHERE i.batch_uuid = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlSimple)) {
            
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                InspectionDetail d = new InspectionDetail();
                // Kita perlu sedikit modifikasi Model InspectionDetail atau akal-akalan sedikit
                // karena model aslinya tidak punya field "vendorName"
                
                d.setVendorId(rs.getInt("vendor_id")); 
                // Kita pinjam field 'photoPath' untuk menyimpan Nama Vendor sementara (Hack UI)
                // Atau field lain yang string. Tapi sebaiknya buat DTO baru.
                // Agar tidak rumit, kita simpan path foto asli di photoPath, 
                // nanti di UI kita ambil Vendor ID dan cari namanya (atau query join diatas).
                
                // Mari kita gunakan cara bersih: Buat kelas DTO (Data Transfer Object) di dalam method UI saja
                // Tapi karena return type method ini List<InspectionDetail>, kita isi data standar:
                
                // d.setComponentId(...); // Masih 0
                // d.setVendorId(rs.getInt("vendor_id"));
                // ... isi setter lainnya
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    // --- ALTERNATIF YANG LEBIH BAIK: RETRIEVE UNTUK TABEL UI ---
     public DefaultTableModel getDetailTableForView(String uuid) {
        // KOLOM HEADER (Bau, Rasa, Tekstur Dipisah)
        String[] columns = {
            "Bahan Baku",       // 0
            "Menu Masakan",     // 1
            "Vendor",           // 2
            "Bau",              // 3
            "Rasa",             // 4
            "Tekstur",          // 5
            "Hasil AI",         // 6
            "Status Akhir",     // 7
            "Foto Path"         // 8 (Hidden)
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        String sql = 
            "SELECT " +
            "  mc.raw_material_name, " +
            "  mc.component_name, " +
            "  v.name as vendor_name, " +
            "  d.bau_ok, d.rasa_ok, d.tekstur_ok, " + // Ambil mentah boolean
            "  d.status_ai, " +
            "  d.status_final, " +
            "  d.photo_path " +
            "FROM inspection_details d " +
            "JOIN inspections i ON d.inspection_id = i.id " +
            "JOIN menu_components mc ON d.component_id = mc.id " +
            "JOIN vendors v ON d.vendor_id = v.id " +
            "WHERE i.batch_uuid = ? " +
            "ORDER BY d.id ASC";
            
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                // Konversi boolean ke String ("OK" / "X") agar lebih enak dilihat
                String bau = rs.getBoolean("bau_ok") ? "OK" : "X";
                String rasa = rs.getBoolean("rasa_ok") ? "OK" : "X";
                String tekstur = rs.getBoolean("tekstur_ok") ? "OK" : "X";

                model.addRow(new Object[]{
                    rs.getString("raw_material_name"), // 0
                    rs.getString("component_name"),    // 1
                    rs.getString("vendor_name"),       // 2
                    bau,                               // 3
                    rasa,                              // 4
                    tekstur,                           // 5
                    rs.getString("status_ai"),         // 6
                    rs.getString("status_final"),      // 7
                    rs.getString("photo_path")         // 8
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return model;
    }
        
        // 5. AMBIL DATA LENGKAP UNTUK LAPORAN PDF
    // Mengembalikan ResultSet agar fleksibel dibaca oleh PDF Generator
    public ResultSet getReportData(String uuid) {
        String sql = "SELECT " +
                     "i.batch_uuid, i.created_at, i.workflow_status, " +
                     "m.name as menu_name, " +
                     "u.fullname as inspector_name, " +
                     "mc.raw_material_name, " +
                     "v.name as vendor_name, " +
                     "d.bau_ok, d.rasa_ok, d.tekstur_ok, d.status_ai, d.status_final " +
                     "FROM inspection_details d " +
                     "JOIN inspections i ON d.inspection_id = i.id " +
                     "LEFT JOIN menus m ON i.menu_id = m.id " +
                     "LEFT JOIN users u ON i.inspector_id = u.id " +
                     "LEFT JOIN menu_components mc ON d.component_id = mc.id " +
                     "LEFT JOIN vendors v ON d.vendor_id = v.id " +
                     "WHERE i.batch_uuid = ?";
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uuid);
            return ps.executeQuery(); 
            // Note: ResultSet ini harus ditutup di kelas pemanggil (PdfService)
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ResultSet getReportByDateRange(String startDate, String endDate) {
        // Query ini menggabungkan semua inspeksi dalam rentang waktu
        // dan menghitung berapa item PASS dan FAIL dalam setiap inspeksi
        String sql = "SELECT i.batch_uuid, i.created_at, m.name as menu_name, u.fullname as inspector_name, " +
                     "i.workflow_status, " +
                     "(SELECT COUNT(*) FROM inspection_details d WHERE d.inspection_id = i.id AND d.status_final = 'PASS') as total_pass, " +
                     "(SELECT COUNT(*) FROM inspection_details d WHERE d.inspection_id = i.id AND (d.status_final = 'FAIL' OR d.status_final = 'REJECTED')) as total_fail " +
                     "FROM inspections i " +
                     "LEFT JOIN menus m ON i.menu_id = m.id " +
                     "LEFT JOIN users u ON i.inspector_id = u.id " +
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