package com.sentragizi.modules.inspector.ui.wizard;

import com.sentragizi.infrastructure.config.AppConfig;
import com.sentragizi.modules.inspector.repositories.InspectionRepository;
import com.sentragizi.modules.inspector.ui.InspectorMainFrame;
import com.sentragizi.shared.models.Menu;
import com.sentragizi.shared.utils.FileUploader; // Wajib Import
import com.sentragizi.shared.utils.SessionManager;
import com.sentragizi.infrastructure.database.DatabaseConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;
import java.util.UUID;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException; 
import java.io.File;

public class PanelStage1 extends javax.swing.JPanel {
    InspectorMainFrame frame;
    InspectionRepository repo = new InspectionRepository();
    DefaultTableModel tableModel;

    private String currentOriginalFileName = "";
    public PanelStage1(InspectorMainFrame f) {
        this.frame = f;
        initComponents();
        setupTable();
        loadCombo();
    }
    
    private void setupTable() {
        // Kolom: Nama Bahan, Status, Path Foto
        tableModel = new DefaultTableModel(new String[]{"Nama Bahan", "Status", "Path Foto"}, 0) {
            @Override // Biar tabel gak bisa diedit manual
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblChecklist.setModel(tableModel);
    }
    
    // Load Menu & Vendor (Kode sama kayak sebelumnya)
    private void loadCombo() {
       try {
            Connection conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();
            
            cmbMenu.removeAllItems();
            ResultSet rsM = st.executeQuery("SELECT * FROM menus");
            while(rsM.next()) {
                cmbMenu.addItem(new Menu(rsM.getInt("id"), rsM.getString("name")));
            }
            
            cmbVendor.removeAllItems();
            ResultSet rsV = st.executeQuery("SELECT * FROM vendors");
            while(rsV.next()) {
                cmbVendor.addItem(rsV.getInt("id") + " - " + rsV.getString("name"));
            }
        } catch(Exception e) { e.printStackTrace(); }
    }

    
    
    private void simpanData(String status, String notes) {
        try {
            String uuid = UUID.randomUUID().toString();
            Menu m = (Menu) cmbMenu.getSelectedItem();
            String vendorRaw = cmbVendor.getSelectedItem().toString();
            int vendorId = Integer.parseInt(vendorRaw.split(" - ")[0]);
            int inspectorId = SessionManager.getCurrentUser() != null 
        ? SessionManager.getCurrentUser().getId() 
        : 1;
            
            // Ambil path foto pertama yang sudah di Htdocs
            String samplePhoto = tableModel.getValueAt(0, 2).toString();
            
            repo.startBatch(uuid, m.getId(), inspectorId, vendorId, samplePhoto, "PASS", "Semua bahan segar.");
            
            JOptionPane.showMessageDialog(this, "Semua bahan tervalidasi! Masuk antrean masak.");
            frame.showPage("QUEUE");
            
        } catch (Exception e) { 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal Simpan Database: " + e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmbMenu = new javax.swing.JComboBox<>();
        cmbVendor = new javax.swing.JComboBox<>();
        txtFoto = new javax.swing.JTextField();
        btnCekItem = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblChecklist = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnSimpan = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(552, 383));

        cmbMenu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMenuActionPerformed(evt);
            }
        });

        cmbVendor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnCekItem.setText("Cek Item");
        btnCekItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCekItemActionPerformed(evt);
            }
        });

        btnBatal.setText("Kembali");
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });

        tblChecklist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblChecklist);

        jLabel1.setText("Menu:");

        jLabel2.setText("Vendor:");

        jLabel3.setText("Gambar:");

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI Emoji", 1, 18)); // NOI18N
        jLabel4.setText("Tahap 1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel2)
                                                .addComponent(jLabel3))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(7, 7, 7)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(cmbVendor, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cmbMenu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(txtFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(btnBatal)
                                        .addGap(58, 58, 58)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnSimpan)
                                            .addComponent(btnCekItem)))))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(224, 224, 224)
                        .addComponent(jLabel4)))
                .addContainerGap(136, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmbMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbVendor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCekItem)))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBatal)
                    .addComponent(btnSimpan))
                .addGap(32, 32, 32))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMenuActionPerformed
        Menu m = (Menu) cmbMenu.getSelectedItem();
        if (m == null) return;
        
        // Reset Tabel
        tableModel.setRowCount(0);
        
        // Ambil komponen yang perlu dicek dari DB
        List<String> rawItems = repo.getRawChecklist(m.getId());
        
        if (rawItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Menu ini tidak butuh cek bahan mentah.");
        } else {
            for(String item : rawItems) {
                // Masukkan ke tabel: Nama Item, Status Pending, Path Kosong
                tableModel.addRow(new Object[]{item, "PENDING", ""});
            }
        }
    }//GEN-LAST:event_cmbMenuActionPerformed

    private void btnCekItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCekItemActionPerformed
        int row = tblChecklist.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris bahan di tabel dulu!");
            return;
        }
        
        String itemName = tableModel.getValueAt(row, 0).toString();
        
        String sourcePath = "";
        
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            sourcePath = fc.getSelectedFile().getAbsolutePath();
            // >>> PERUBAHAN: Simpan nama file asli <<<
            currentOriginalFileName = fc.getSelectedFile().getName(); 
            txtFoto.setText(sourcePath); 
        } else {
            return;
        }
        
        String fullPathWeb = "";
        try {
            // A. Arsip Web (Path ini akan berisi UUID/ID Acak)
            String webFileName = FileUploader.copyFile(sourcePath, AppConfig.DIR_UPLOAD_RAW);
            fullPathWeb = AppConfig.DIR_UPLOAD_RAW + webFileName;
            
            // B. Arsip Lokal
            FileUploader.copyFile(sourcePath, AppConfig.DIR_ARCHIVE_RAW); 
            
            // >>> PERUBAHAN: Kirim 3 Parameter ke Python <<<
            // 1. Path file (yang sudah di-rename jadi UUID)
            // 2. Item yang dicari
            // 3. Nama file asli (untuk verifikasi kata kunci "Mocking")
            ProcessBuilder pb = new ProcessBuilder(
                AppConfig.PYTHON_EXEC,
                AppConfig.SCRIPT_FRESHNESS,
                fullPathWeb,            
                itemName,
                currentOriginalFileName 
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String jsonStr = reader.readLine();
            
            if (jsonStr == null || !jsonStr.trim().startsWith("{")) {
                 JOptionPane.showMessageDialog(this, "Python Error: " + jsonStr);
                 return;
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonStr);
            String status = (String) json.get("status");
            String notes = (String) json.get("notes");
            
            if ("PASS".equals(status)) {
                tableModel.setValueAt("✅ LOLOS", row, 1);
                tableModel.setValueAt(fullPathWeb, row, 2); 
                JOptionPane.showMessageDialog(this, "AI: " + notes);
            } else if ("WRONG_ITEM".equals(status)) {
                new File(fullPathWeb).delete();
                JOptionPane.showMessageDialog(this, "SALAH BARANG!\n" + notes, "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                new File(fullPathWeb).delete();
                tableModel.setValueAt("❌ BUSUK", row, 1);
                JOptionPane.showMessageDialog(this, "AI: " + notes, "GAGAL", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "GAGAL SALIN FILE: Cek izin folder.\n" + e.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnCekItemActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // Cek apakah semua baris sudah LOLOS?
        if (tableModel.getRowCount() == 0) return;
        
        for(int i=0; i<tableModel.getRowCount(); i++) {
            String status = tableModel.getValueAt(i, 1).toString();
            if (!status.equals("✅ LOLOS")) {
                JOptionPane.showMessageDialog(this, "Semua item harus berstatus LOLOS sebelum disimpan!");
                return;
            }
        }
        
        // Simpan Data
        try {
            String uuid = UUID.randomUUID().toString();
            Menu m = (Menu) cmbMenu.getSelectedItem();
            String vendorRaw = cmbVendor.getSelectedItem().toString();
            int vendorId = Integer.parseInt(vendorRaw.split(" - ")[0]);
            int inspectorId = SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : 1; 
            
            // Ambil path foto pertama saja sebagai perwakilan (atau gabung stringnya)
            String samplePhoto = tableModel.getValueAt(0, 2).toString();
            
            repo.startBatch(uuid, m.getId(), inspectorId, vendorId, samplePhoto, "PASS", "Semua bahan segar.");
            
            JOptionPane.showMessageDialog(this, "Semua bahan tervalidasi! Masuk antrean masak.");
            frame.showPage("QUEUE");
            
        } catch (Exception e) { e.printStackTrace(); }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        // TODO add your handling code here:
        frame.showPage("QUEUE");
    }//GEN-LAST:event_btnBatalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnCekItem;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JComboBox<Object> cmbMenu;
    private javax.swing.JComboBox<String> cmbVendor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblChecklist;
    private javax.swing.JTextField txtFoto;
    // End of variables declaration//GEN-END:variables
}
