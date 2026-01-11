package com.sentragizi.modules.auth.services;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.User;
import com.sentragizi.shared.utils.SessionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    
    public boolean login(String username, String password) {
        
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password); 
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                
                
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role"), 
                    rs.getString("fullname")
                );
                
                
                SessionManager.login(user);
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false; 
    }
}