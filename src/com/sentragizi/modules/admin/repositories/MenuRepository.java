package com.sentragizi.modules.admin.repositories;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.Menu;
import com.sentragizi.shared.models.MenuComponent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    
    public boolean saveMenuWithComponents(String menuName, List<MenuComponent> components) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            
            String sqlMenu = "INSERT INTO menus (name) VALUES (?)";
            PreparedStatement psMenu = conn.prepareStatement(sqlMenu, Statement.RETURN_GENERATED_KEYS);
            psMenu.setString(1, menuName);
            psMenu.executeUpdate();

            
            ResultSet rs = psMenu.getGeneratedKeys();
            int menuId = 0;
            if (rs.next()) {
                menuId = rs.getInt(1);
            }

            
            
            String sqlComp = "INSERT INTO menu_components " +
                             "(menu_id, component_name, ai_label, is_optional, needs_raw_check, raw_material_name) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement psComp = conn.prepareStatement(sqlComp);

            for (MenuComponent comp : components) {
                psComp.setInt(1, menuId);
                psComp.setString(2, comp.getComponentName());
                
                
                psComp.setString(3, comp.getAiLabel());      
                psComp.setBoolean(4, comp.isOptional());     
                
                
                psComp.setBoolean(5, comp.isNeedsRawCheck());
                psComp.setString(6, comp.getRawMaterialName());
                
                psComp.addBatch();
            }
            psComp.executeBatch();

            conn.commit(); 
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ex) {}
        }
    }

    
    
    public List<MenuComponent> getComponentsByMenuId(int menuId) {
        List<MenuComponent> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_components WHERE menu_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, menuId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                
                list.add(new MenuComponent(
                    rs.getInt("id"),
                    rs.getInt("menu_id"),
                    rs.getString("component_name"),
                    rs.getString("ai_label"),      
                    rs.getBoolean("is_optional"),  
                    rs.getBoolean("needs_raw_check"),
                    rs.getString("raw_material_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    
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

    
    public boolean deleteMenuComponents(int menuId) {
        
        String checkSql = "SELECT COUNT(*) FROM inspection_details d " +
                          "JOIN menu_components c ON d.component_id = c.id " +
                          "WHERE c.menu_id = ?";
                          
        String deleteSql = "DELETE FROM menu_components WHERE menu_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, menuId);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        
                        
                        System.out.println("Gagal Hapus: Ada " + count + " riwayat inspeksi terkait.");
                        return false; 
                    }
                }
            }

            
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, menuId);
                psDelete.executeUpdate();
                return true; 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
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

    
    public boolean deleteMenu(int menuId) {
        
        deleteMenuComponents(menuId);
        
        String sql = "DELETE FROM menus WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, menuId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
