package com.sentragizi;

import com.sentragizi.infrastructure.config.AppConfig;
import com.sentragizi.infrastructure.database.DatabaseConnection;
import java.io.File;

public class TestInfra {
    public static void main(String[] args) {
        System.out.println("=== TEST INFRASTRUKTUR ===");
        
        // 1. Cek Python
        File py = new File(AppConfig.PYTHON_EXEC);
        if (py.exists()) {
            System.out.println("✅ Python Ditemukan: " + py.getAbsolutePath());
        } else {
            System.out.println("❌ Python TIDAK DITEMUKAN! Cek folder python_embed.");
        }
        
        // 2. Cek Database
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✅ Koneksi Database SUKSES!");
        } else {
            System.out.println("❌ Koneksi Database GAGAL! Cek XAMPP.");
        }
    }
}