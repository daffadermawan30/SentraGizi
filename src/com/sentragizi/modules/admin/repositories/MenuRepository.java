package com.sentragizi.modules.admin.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.Menu;
import com.sentragizi.shared.models.MenuComponent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    // 1. FUNGSI SIMPAN (UPDATE: Tambah ai_label & is_optional)
    public boolean saveMenuWithComponents(String menuName, List<MenuComponent> components) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // MULAI TRANSAKSI

            // A. Simpan Header Menu
            String sqlMenu = "INSERT INTO menus (name) VALUES (?)";
            PreparedStatement psMenu = conn.prepareStatement(sqlMenu, Statement.RETURN_GENERATED_KEYS);
            psMenu.setString(1, menuName);
            psMenu.executeUpdate();

            // Ambil ID Menu baru
            ResultSet rs = psMenu.getGeneratedKeys();
            int menuId = 0;
            if (rs.next()) {
                menuId = rs.getInt(1);
            }

            // B. Simpan Detail Komponen (UPDATE QUERY DISINI)
            // Kita tambah kolom ai_label dan is_optional
            String sqlComp = "INSERT INTO menu_components " +
                             "(menu_id, component_name, ai_label, is_optional, needs_raw_check, raw_material_name) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement psComp = conn.prepareStatement(sqlComp);

            for (MenuComponent comp : components) {
                psComp.setInt(1, menuId);
                psComp.setString(2, comp.getComponentName());
                
                // --- UPDATE PENTING ---
                psComp.setString(3, comp.getAiLabel());      // Simpan label AI (misal: "ayam_goreng")
                psComp.setBoolean(4, comp.isOptional());     // Simpan status Opsional
                // ----------------------
                
                psComp.setBoolean(5, comp.isNeedsRawCheck());
                psComp.setString(6, comp.getRawMaterialName());
                
                psComp.addBatch();
            }
            psComp.executeBatch();

            conn.commit(); // SIMPAN PERMANEN
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ex) {}
        }
    }

    // 2. FUNGSI AMBIL KOMPONEN (WAJIB ADA UNTUK AI ENGINE)
    // Method ini akan dipanggil oleh PanelStage2/ComponentCounter
    public List<MenuComponent> getComponentsByMenuId(int menuId) {
        List<MenuComponent> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_components WHERE menu_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, menuId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Pastikan urutan constructor sesuai dengan Model MenuComponent yang baru
                list.add(new MenuComponent(
                    rs.getInt("id"),
                    rs.getInt("menu_id"),
                    rs.getString("component_name"),
                    rs.getString("ai_label"),      // <--- Ambil Label AI
                    rs.getBoolean("is_optional"),  // <--- Ambil Status Opsional
                    rs.getBoolean("needs_raw_check"),
                    rs.getString("raw_material_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. FUNGSI AMBIL DAFTAR MENU (Untuk Dropdown Pilihan)
    public List<Menu> getAllMenus() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM menus";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                menus.add(new Menu(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menus;
    }
    
    public boolean updateMenuName(int menuId, String newName) {
        String sql = "UPDATE menus SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, menuId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 2. Hapus Semua Komponen (Dipakai saat Update Menu agar bersih)
    public boolean deleteMenuComponents(int menuId) {
        // 1. Cek apakah ada inspeksi yang menggunakan komponen dari menu ini?
        String checkSql = "SELECT COUNT(*) FROM inspection_details d " +
                          "JOIN menu_components c ON d.component_id = c.id " +
                          "WHERE c.menu_id = ?";
                          
        String deleteSql = "DELETE FROM menu_components WHERE menu_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Lakukan pengecekan
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, menuId);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        // ADA RIWAYAT! JANGAN HAPUS.
                        // Kembalikan false agar PanelInputMenu tahu dan memberi peringatan ke user.
                        System.out.println("Gagal Hapus: Ada " + count + " riwayat inspeksi terkait.");
                        return false; 
                    }
                }
            }

            // Jika aman (tidak ada riwayat), baru hapus
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, menuId);
                psDelete.executeUpdate();
                return true; // Berhasil hapus
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Simpan Komponen (Versi tanpa Transaksi, untuk dipakai di Update)
    public boolean saveComponents(int menuId, List<MenuComponent> components) {
        String sql = "INSERT INTO menu_components (menu_id, component_name, ai_label, is_optional, needs_raw_check, raw_material_name) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            for (MenuComponent comp : components) {
                ps.setInt(1, menuId);
                ps.setString(2, comp.getComponentName());
                ps.setString(3, comp.getAiLabel());
                ps.setBoolean(4, comp.isOptional());
                ps.setBoolean(5, comp.isNeedsRawCheck());
                ps.setString(6, comp.getRawMaterialName());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 4. Hapus Menu (Cascading delete biasanya otomatis di DB, tapi kita buat manual biar aman)
    public boolean deleteMenu(int menuId) {
        // Hapus detail dulu (optional jika DB sudah cascade)
        deleteMenuComponents(menuId);
        
        String sql = "DELETE FROM menus WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
