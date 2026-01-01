package com.sentragizi.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Panggil Driver MySQL (Pastikan library mysql-connector ada)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Buka Pintu ke Database 'sentragizi_db'
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/rev_sentragizi_db", "root", "");
            
        } catch (Exception e) {
            System.err.println("KONEKSI DATABASE GAGAL! Cek XAMPP.");
            e.printStackTrace();
        }
        return conn;
    }
}