package com.sentragizi.modules.inspector.ui.wizard;

import com.sentragizi.infrastructure.config.AppConfig;
import com.sentragizi.modules.ai_engine.ComponentCounter;
import com.sentragizi.modules.inspector.repositories.InspectionRepository;
import com.sentragizi.modules.inspector.services.QRCodeService;
import com.sentragizi.modules.inspector.ui.InspectorMainFrame;
import com.sentragizi.shared.utils.FileUploader;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PanelStage2 extends javax.swing.JPanel {
    InspectorMainFrame frame;
    String currentUUID;
    InspectionRepository repo = new InspectionRepository();
    boolean isPassed = false; 
    private String tempUploadPath = "";
    

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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(648, 311));

        lblInfo.setText("Batch Id");

        txtBerat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBeratActionPerformed(evt);
            }
        });

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

        jLabel1.setText("Koki:");

        jLabel2.setText("Suhu:");

        jLabel3.setText("Berat:");

        jLabel4.setText("Gambar:");

        jLabel5.setFont(new java.awt.Font("Segoe UI Emoji", 1, 18)); // NOI18N
        jLabel5.setText("Tahap 2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnUpload, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(14, 14, 14)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3))
                                    .addGap(107, 107, 107)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtSuhu, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtKoki, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addComponent(btnSimpan)
                                        .addGap(46, 46, 46)
                                        .addComponent(btnCek)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(219, 219, 219)
                        .addComponent(jLabel5))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblInfo)))
                .addContainerGap(169, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtKoki, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtSuhu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtBerat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFoto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(btnUpload))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCek)
                    .addComponent(btnSimpan))
                .addGap(4, 4, 4)
                .addComponent(lblInfo)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            tempUploadPath = fc.getSelectedFile().getAbsolutePath();
            txtFoto.setText(tempUploadPath);
        }
    }//GEN-LAST:event_btnUploadActionPerformed

    private void btnCekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCekActionPerformed
        if (tempUploadPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan upload foto terlebih dahulu!");
            return;
        }
        
        // Perluas logika: Jika belum disalin, kita copy ke temp folder Web untuk dicek AI
        
        try {
            // Kita tidak perlu menyalinnya ke tempat final sebelum dicek, kita langsung pakai path asli untuk cek AI
            // Atau jika perlu path Web untuk Python, kita lakukan seperti di Stage 1:
            // String fullPathWeb = FileUploader.copyFile(tempUploadPath, AppConfig.DIR_UPLOAD_COOKED);
            // new File(fullPathWeb).delete(); // Hapus setelah cek

            // Langsung panggil Python dengan path asli karena Python hanya membaca data.
            ProcessBuilder pb = new ProcessBuilder(
                AppConfig.PYTHON_EXEC,
                AppConfig.SCRIPT_COUNTING,
                tempUploadPath // Kirim path asli
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String jsonStr = r.readLine();
            
            if (jsonStr == null || !jsonStr.trim().startsWith("{")) {
                 JOptionPane.showMessageDialog(this, "Error dari Python: " + jsonStr);
                 return;
            }
            
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonStr);
            JSONObject counts = (JSONObject) json.get("counts");
            
            int menuId = repo.getMenuIdByUUID(currentUUID);
            List<String> required = repo.getAllComponents(menuId);
            
            ComponentCounter counter = new ComponentCounter();
            String analysisResult = counter.compareComponents(required, counts);
            
            txtHasilAI.setText(analysisResult);
            
            if (analysisResult.contains("PERINGATAN")) {
                JOptionPane.showMessageDialog(this, "PERINGATAN: Komponen tidak lengkap!", "Indikasi Korupsi", JOptionPane.WARNING_MESSAGE);
                isPassed = false; 
            } else {
                JOptionPane.showMessageDialog(this, "SEMPURNA! Komponen Lengkap.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                isPassed = true;
            }
            btnSimpan.setEnabled(true);
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan cek AI: " + e.getMessage());
        }
    }//GEN-LAST:event_btnCekActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        if (tempUploadPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Foto masakan harus diupload!");
            return;
        }
        
        try {
            double suhu = Double.parseDouble(txtSuhu.getText());
            int berat = Integer.parseInt(txtBerat.getText());
            
            if(suhu < 75 || berat < 400) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "PERINGATAN: Suhu/Berat dibawah standar!\nTetap simpan?", 
                    "Konfirmasi Simpan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) return;
            }
            
            // 1. Simpan Foto Masakan (Web & Lokal)
            String webFileName = FileUploader.copyFile(tempUploadPath, AppConfig.DIR_UPLOAD_COOKED);
            String finalSavedPath = AppConfig.DIR_UPLOAD_COOKED + webFileName;
            FileUploader.copyFile(tempUploadPath, AppConfig.DIR_ARCHIVE_COOKED);
            
            // 2. Simpan Database
            String status = isPassed ? "COMPLETED" : "REJECTED_FINAL";
            repo.saveFinal(currentUUID, txtKoki.getText(), suhu, berat, finalSavedPath, txtHasilAI.getText(), status);
            
            // 3. Generate QR Code
            QRCodeService qr = new QRCodeService();
            String pathQRWeb = AppConfig.DIR_QR + currentUUID + ".png"; // Path XAMPP
            String qrUrl = AppConfig.BASE_URL + "index.php?id=" + currentUUID;
            
            boolean qrSuccess = qr.generate(qrUrl, pathQRWeb);
            
            if (qrSuccess) {
                // >>> PERUBAHAN UTAMA: COPY QR KE LOKAL <<<
                String pathQRLocal = AppConfig.DIR_ARCHIVE_QR + currentUUID + ".png";
                Files.copy(
                    new File(pathQRWeb).toPath(), 
                    new File(pathQRLocal).toPath(), 
                    StandardCopyOption.REPLACE_EXISTING
                );
                
                // Tampilkan QR Code di Pop-up
                ImageIcon icon = new ImageIcon(pathQRLocal); 
                Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                
                JOptionPane.showMessageDialog(this, 
                    "SELESAI! Data tersimpan & QR Code dicadangkan di lokal.", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(img));
            } else {
                JOptionPane.showMessageDialog(this, "Data tersimpan, tapi GAGAL membuat QR Code.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            
            frame.showPage("QUEUE");
            
        } catch(NumberFormatException e) { 
            JOptionPane.showMessageDialog(this, "Input suhu/berat harus angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Simpan: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void txtBeratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBeratActionPerformed

    }//GEN-LAST:event_txtBeratActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCek;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnUpload;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JTextField txtBerat;
    private javax.swing.JTextField txtFoto;
    private javax.swing.JTextArea txtHasilAI;
    private javax.swing.JTextField txtKoki;
    private javax.swing.JTextField txtSuhu;
    // End of variables declaration//GEN-END:variables
}
