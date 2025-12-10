package com.sentragizi.modules.admin.ui.panels;

import com.sentragizi.modules.admin.repositories.MenuRepository;
import com.sentragizi.shared.models.MenuComponent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PanelInputMenu extends javax.swing.JPanel {

    DefaultTableModel model;

    public PanelInputMenu() {
        initComponents();
        // Setup Kolom Tabel: Nama Komponen, Perlu Cek?, Nama Bahan Baku
        model = new DefaultTableModel(new String[]{"Komponen", "Cek Mentah?", "Bahan Baku"}, 0);
        tblComponents.setModel(model);
    }

  
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtMenuName = new javax.swing.JTextField();
        txtCompName = new javax.swing.JTextField();
        chkRaw = new javax.swing.JCheckBox();
        txtRawName = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblComponents = new javax.swing.JTable();
        btnSaveAll = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        txtMenuName.setText("jTextField1");

        txtCompName.setText("jTextField1");

        chkRaw.setText("Cek Mentah");
        chkRaw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRawActionPerformed(evt);
            }
        });

        txtRawName.setText("jTextField1");

        btnAdd.setText("Tambahkan");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        tblComponents.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblComponents);

        btnSaveAll.setText("jButton2");
        btnSaveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkRaw)
                    .addComponent(txtCompName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRawName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd))
                .addGap(46, 46, 46)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMenuName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveAll))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(txtMenuName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCompName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkRaw)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRawName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnSaveAll)
                .addGap(65, 65, 65))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        String comp = txtCompName.getText();
        boolean check = chkRaw.isSelected();
        String raw = txtRawName.getText();
        
        if (comp.isEmpty()) { JOptionPane.showMessageDialog(this, "Isi nama komponen!"); return; }
        if (check && raw.isEmpty()) { JOptionPane.showMessageDialog(this, "Jika dicek mentah, isi nama bahan bakunya!"); return; }

        // Masukkan ke tabel visual
        model.addRow(new Object[]{comp, check, raw});
        
        // Bersihkan input
        txtCompName.setText("");
        txtRawName.setText("");
        chkRaw.setSelected(false);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnSaveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveAllActionPerformed
        // TODO add your handling code here:
        String menuName = txtMenuName.getText();
        
        if (menuName.isEmpty()) { JOptionPane.showMessageDialog(this, "Isi Nama Paket Menu!"); return; }
        if (model.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Minimal harus ada 1 komponen!"); return; }

        // Ambil data dari Tabel Visual -> Masukkan ke List Java
        List<MenuComponent> list = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String cName = model.getValueAt(i, 0).toString();
            boolean cCheck = (boolean) model.getValueAt(i, 1);
            String rName = model.getValueAt(i, 2).toString();
            
            // Masukkan ke objek MenuComponent
            list.add(new MenuComponent(cName, cCheck, rName));
        }

        // Kirim ke Repository
        MenuRepository repo = new MenuRepository();
        if (repo.saveMenuWithComponents(menuName, list)) {
            JOptionPane.showMessageDialog(this, "SUKSES! Menu & Rinciannya tersimpan.");
            model.setRowCount(0); // Reset tabel
            txtMenuName.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal simpan ke database.");
        }
    }//GEN-LAST:event_btnSaveAllActionPerformed

    private void chkRawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRawActionPerformed
        // TODO add your handling code here:
        txtRawName.setEnabled(chkRaw.isSelected());
        if (!chkRaw.isSelected()) txtRawName.setText("");
    }//GEN-LAST:event_chkRawActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnSaveAll;
    private javax.swing.JCheckBox chkRaw;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblComponents;
    private javax.swing.JTextField txtCompName;
    private javax.swing.JTextField txtMenuName;
    private javax.swing.JTextField txtRawName;
    // End of variables declaration//GEN-END:variables
}
