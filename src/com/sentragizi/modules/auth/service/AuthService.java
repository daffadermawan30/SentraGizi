package com.sentragizi.modules.auth.services;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.shared.models.User;
import com.sentragizi.shared.utils.SessionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    
    public boolean login(String username, String password) {
        // Query cek user
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password); // Note: Di real app, gunakan Hashing (BCrypt)
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // LOGIN SUKSES!
                // 1. Buat objek User
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role"), // "ADMIN" atau "INSPECTOR"
                    rs.getString("fullname")
                );
                
                // 2. Simpan ke Session (Biar aplikasi tahu siapa yang login)
                SessionManager.login(user);
                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false; // Login Gagal
    }
}