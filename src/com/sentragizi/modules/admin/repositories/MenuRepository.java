package com.sentragizi.modules.admin.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.MenuComponent;
import java.sql.*;
import java.util.List;

public class MenuRepository {

    public boolean saveMenuWithComponents(String menuName, List<MenuComponent> components) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // MULAI TRANSAKSI

            // 1. Simpan Header Menu
            String sqlMenu = "INSERT INTO menus (name) VALUES (?)";
            PreparedStatement psMenu = conn.prepareStatement(sqlMenu, Statement.RETURN_GENERATED_KEYS);
            psMenu.setString(1, menuName);
            psMenu.executeUpdate();

            // Ambil ID Menu yang baru dibuat
            ResultSet rs = psMenu.getGeneratedKeys();
            int menuId = 0;
            if (rs.next()) {
                menuId = rs.getInt(1);
            }

            // 2. Simpan Detail Komponen (Batch Insert)
            String sqlComp = "INSERT INTO menu_components (menu_id, component_name, needs_raw_check, raw_material_name) VALUES (?, ?, ?, ?)";
            PreparedStatement psComp = conn.prepareStatement(sqlComp);

            for (MenuComponent comp : components) {
                psComp.setInt(1, menuId);
                psComp.setString(2, comp.getComponentName());
                psComp.setBoolean(3, comp.isNeedsRawCheck());
                psComp.setString(4, comp.getRawMaterialName()); // Bisa null jika false
                psComp.addBatch();
            }
            psComp.executeBatch(); // Eksekusi sekaligus

            conn.commit(); // SIMPAN PERMANEN
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {} // Batalkan jika error
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ex) {}
        }
    }
}