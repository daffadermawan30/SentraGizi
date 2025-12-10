package com.sentragizi.modules.inspector.ui.wizard;

import com.sentragizi.infrastructure.config.AppConfig;
import com.sentragizi.modules.ai_engine.ComponentCounter;
import com.sentragizi.modules.inspector.repositories.InspectionRepository;
import com.sentragizi.modules.inspector.services.QRCodeService;
import com.sentragizi.modules.inspector.ui.InspectorMainFrame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PanelStage2 extends javax.swing.JPanel {
    InspectorMainFrame frame;
    String currentUUID;
    InspectionRepository repo = new InspectionRepository();
    boolean isPassed = false; 

    public PanelStage2(InspectorMainFrame f) {
        this.frame = f;
        initComponents();
    }
    
    // Method dipanggil saat pindah halaman
    public void setUUID(String uuid) {
        this.currentUUID = uuid;
        lblInfo.setText("Batch ID: " + uuid);
        btnSimpan.setEnabled(false); // Kunci dulu
        txtHasilAI.setText("");
        txtFoto.setText("");
        txtKoki.setText("");
        txtSuhu.setText("");
        txtBerat.setText("");
        isPassed = false;
    }

    // Tombol Upload (Sama kayak biasa)
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblInfo = new javax.swing.JLabel();
        txtKoki = new javax.swing.JTextField();
        txtSuhu = new javax.swing.JTextField();
        txtBerat = new javax.swing.JTextField();
        txtFoto = new javax.swing.JTextField();
        btnUpload = new javax.swing.JButton();
        btnCek = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtHasilAI = new javax.swing.JTextArea();
        btnSimpan = new javax.swing.JButton();

        lblInfo.setText("Batch Id");

        txtKoki.setText("Koki");

        txtSuhu.setText("Suhu");

        txtBerat.setText("Berat");

        btnUpload.setText("upload");
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });

        btnCek.setText("Cek");
        btnCek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCekActionPerformed(evt);
            }
        });

        txtHasilAI.setColumns(20);
        txtHasilAI.setRows(5);
        jScrollPane1.setViewportView(txtHasilAI);

        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lblInfo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(158, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSuhu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtKoki, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(129, 129, 129))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtFoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpload)
                        .addGap(66, 66, 66))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnCek)
                        .addGap(119, 119, 119))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSimpan)
                        .addGap(116, 116, 116))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfo)
                .addGap(31, 31, 31)
                .addComponent(txtKoki, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtSuhu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpload))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCek)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSimpan)
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
            txtFoto.setText(fc.getSelectedFile().getAbsolutePath());
    }//GEN-LAST:event_btnUploadActionPerformed

    private void btnCekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCekActionPerformed
        if (txtFoto.getText().isEmpty()) return;
        
        try {
            // 1. Panggil Python Counting
            ProcessBuilder pb = new ProcessBuilder(
                AppConfig.PYTHON_EXEC,
                AppConfig.SCRIPT_COUNTING,
                txtFoto.getText()
            );
            pb.redirectErrorStream(true); // Gabungkan error stream
            Process p = pb.start();
            
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String jsonStr = r.readLine();
            
            // Tambahkan validasi jika output kosong atau error
            if (jsonStr == null || !jsonStr.trim().startsWith("{")) {
                 StringBuilder errorOutput = new StringBuilder();
                 String line;
                 while ((line = r.readLine()) != null) {
                     errorOutput.append(line).append("\n");
                 }
                 JOptionPane.showMessageDialog(this, "Error dari Python: " + (jsonStr != null ? jsonStr : "") + "\n" + errorOutput.toString());
                 return;
            }
            
            // 2. Parse JSON
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonStr);
            JSONObject counts = (JSONObject) json.get("counts");
            
            // 3. Ambil Data Wajib dari DB
            int menuId = repo.getMenuIdByUUID(currentUUID);
            List<String> required = repo.getAllComponents(menuId);
            
            // 4. Bandingkan
            ComponentCounter counter = new ComponentCounter();
            String analysisResult = counter.compareComponents(required, counts);
            
            txtHasilAI.setText(analysisResult);
            
            if (analysisResult.contains("PERINGATAN")) {
                JOptionPane.showMessageDialog(this, "PERINGATAN: Komponen tidak lengkap!", "Indikasi Korupsi", JOptionPane.WARNING_MESSAGE);
                isPassed = false; // Gagal, tapi tombol simpan dibuka biar bisa Override manual
            } else {
                JOptionPane.showMessageDialog(this, "SEMPURNA! Komponen Lengkap.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                isPassed = true;
            }
            btnSimpan.setEnabled(true);
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }//GEN-LAST:event_btnCekActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        try {
            double suhu = Double.parseDouble(txtSuhu.getText());
            int berat = Integer.parseInt(txtBerat.getText());
            
            if(suhu < 75 || berat < 400) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "PERINGATAN: Suhu (" + suhu + "C) atau Berat (" + berat + "g) dibawah standar!\nApakah Anda yakin ingin tetap menyimpan?", 
                    "Konfirmasi Simpan", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            String status = isPassed ? "COMPLETED" : "REJECTED_FINAL";
            // Pastikan Anda memanggil saveFinal dengan parameter yang benar sesuai definisi di InspectionRepository
            repo.saveFinal(currentUUID, txtKoki.getText(), suhu, berat, txtFoto.getText(), txtHasilAI.getText(), status);
            
            // Generate QR
            QRCodeService qr = new QRCodeService();
            // Gunakan separator file yang benar atau "/" yang aman di Java
            String pathQR = AppConfig.DIR_QR + currentUUID + ".png";
            
            // Ganti localhost dengan IP jika ingin diakses dari device lain
            String qrUrl = "http://localhost/sentragizi/index.php?id=" + currentUUID;
            
            boolean qrSuccess = qr.generate(qrUrl, pathQR);
            
            if (qrSuccess) {
                JOptionPane.showMessageDialog(this, "SELESAI! Data tersimpan & QR Code tercetak.\nLokasi: " + pathQR);
            } else {
                JOptionPane.showMessageDialog(this, "Data tersimpan, tapi GAGAL membuat QR Code.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            
            frame.showPage("QUEUE"); // Balik ke antrean
            
        } catch(NumberFormatException e) { 
            JOptionPane.showMessageDialog(this, "Input suhu atau berat harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSimpanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCek;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUpload;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JTextField txtBerat;
    private javax.swing.JTextField txtFoto;
    private javax.swing.JTextArea txtHasilAI;
    private javax.swing.JTextField txtKoki;
    private javax.swing.JTextField txtSuhu;
    // End of variables declaration//GEN-END:variables
}
