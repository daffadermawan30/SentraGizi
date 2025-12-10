package com.sentragizi;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.modules.auth.ui.LoginFrame;
import com.sentragizi.modules.inspector.ui.InspectorMainFrame; // Ganti Import
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class MainApp {

    public static void main(String[] args) {
        
        // Setup Tampilan (Nimbus)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        // Cek Database Sebelum Mulai
        if (DatabaseConnection.getConnection() == null) {
            JOptionPane.showMessageDialog(null, "KONEKSI DATABASE GAGAL! Pastikan XAMPP Nyala.");
            System.exit(0);
        }

        // JALANKAN APLIKASI DARI LOGIN
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}