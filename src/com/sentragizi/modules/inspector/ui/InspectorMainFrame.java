package com.sentragizi.modules.inspector.ui;

import com.sentragizi.modules.inspector.ui.wizard.PanelQueue;
import com.sentragizi.modules.inspector.ui.wizard.PanelStage1;
import com.sentragizi.modules.inspector.ui.wizard.PanelStage2;
import java.awt.CardLayout;
import javax.swing.JPanel;

public class InspectorMainFrame extends javax.swing.JFrame {

    public CardLayout cards;
    public JPanel container; // Biar bisa diakses dari panel lain

    public InspectorMainFrame() {
        initComponents();
        
        container = new JPanel(new CardLayout());
        this.setContentPane(container); // Jadikan panel ini konten utama
        
        cards = (CardLayout) container.getLayout();
        
        // Tambahkan Panel (Kita akan buat PanelQueue & PanelStage1 sebentar lagi)
        // Error merah di sini WAJAR karena filenya belum dibuat. Lanjut saja dulu.
        container.add(new PanelQueue(this), "QUEUE");   
        container.add(new PanelStage1(this), "STAGE1"); 
        container.add(new PanelStage2(this), "STAGE2"); 
        
        cards.show(container, "QUEUE"); // Tampilkan antrean dulu
        this.setSize(800, 600); // Set ukuran default
        
        
    }
    
    // Method Helper untuk pindah halaman
    public void showPage(String pageName) {
        cards.show(container, pageName);
    }
    
    
    public PanelStage2 getPanelStage2() {
    // Cari komponen berdasarkan class (agak tricky di CardLayout, cara gampangnya simpan referensi di variabel)
    for (java.awt.Component comp : container.getComponents()) {
        if (comp instanceof PanelStage2) return (PanelStage2) comp;
    }
    return null;
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InspectorMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InspectorMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InspectorMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InspectorMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InspectorMainFrame().setVisible(true);
            }
        });
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
