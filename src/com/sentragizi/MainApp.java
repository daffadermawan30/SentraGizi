package com.sentragizi;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.modules.auth.ui.LoginFrame;
import com.sentragizi.modules.inspector.ui.InspectorMainFrame; 
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class MainApp {

    public static void main(String[] args) {
        
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        
        if (DatabaseConnection.getConnection() == null) {
            JOptionPane.showMessageDialog(null, "KONEKSI DATABASE GAGAL! Pastikan XAMPP/Laragon Nyala.");
            System.exit(0);
        }

        
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}