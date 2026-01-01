package com.sentragizi.modules.inspector.ui.wizard;

import com.sentragizi.modules.inspector.repositories.InspectionRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class DialogBatchDetail extends JDialog {

    private JTable table;

    public DialogBatchDetail(JFrame parent, String batchUuid) {
        super(parent, "Detail Inspeksi Batch", true);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(250, 250, 250));
        pnlHeader.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Laporan Kualitas Bahan Baku");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(44, 62, 80));

        JLabel lblUuid = new JLabel("Batch ID: " + batchUuid);
        lblUuid.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lblUuid.setForeground(Color.GRAY);

        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblUuid, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABEL ---
        InspectionRepository repo = new InspectionRepository();
        DefaultTableModel model = repo.getDetailTableForView(batchUuid);

        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };
        table.setRowHeight(45); // Sedikit lebih tinggi biar nyaman
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        
        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        // --- PENGATURAN LEBAR KOLOM ---
        if (table.getColumnCount() > 7) {
            table.getColumnModel().getColumn(0).setPreferredWidth(130); // Bahan
            table.getColumnModel().getColumn(1).setPreferredWidth(130); // Masakan
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Vendor
            
            // Kolom Kecil
            table.getColumnModel().getColumn(3).setPreferredWidth(40);
            table.getColumnModel().getColumn(4).setPreferredWidth(40);
            table.getColumnModel().getColumn(5).setPreferredWidth(40);
            
            // Status Akhir (Index 7)
            table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    String status = (String) value;
                    if ("PASS".equals(status)) {
                        c.setForeground(new Color(39, 174, 96)); 
                        setText("✔ AMAN");
                    } else {
                        c.setForeground(new Color(192, 57, 43)); 
                        setText("✖ DITOLAK");
                    }
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
            
            // --- FITUR BARU: MENAMPILKAN LINK FOTO (Index 8) ---
            // Sebelumnya di-hide, sekarang kita munculkan sebagai tombol/link
            TableColumn colFoto = table.getColumnModel().getColumn(8);
            colFoto.setMinWidth(100);
            colFoto.setMaxWidth(100);
            colFoto.setPreferredWidth(100);
            colFoto.setHeaderValue("Bukti Foto"); // Ganti Header jadi lebih jelas
            
            // Renderer Khusus agar terlihat seperti Link/Tombol
            colFoto.setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    String path = (String) value;
                    if (path != null && !path.isEmpty()) {
                        c.setText("<html><font color='blue'><u>📷 Lihat Foto</u></font></html>");
                        c.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        c.setToolTipText("Klik untuk melihat foto bukti");
                    } else {
                        c.setText("-");
                        c.setToolTipText(null);
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
        }
        
        // --- EVENT LISTENER: KLIK UNTUK LIHAT FOTO ---
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                // Jika yang diklik adalah kolom 8 (Foto Path)
                if (row >= 0 && col == 8) {
                    String path = (String) table.getValueAt(row, 8);
                    if (path != null && !path.isEmpty()) {
                        showImagePreview(path); // Panggil fungsi popup gambar
                    } else {
                        JOptionPane.showMessageDialog(DialogBatchDetail.this, "Tidak ada foto tersimpan.");
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 20, 0, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. FOOTER ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JButton btnPrint = new JButton("Cetak PDF");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrint.setBackground(new Color(52, 152, 219));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setPreferredSize(new Dimension(120, 35));
        
        btnPrint.addActionListener(e -> {
             try {
                 JOptionPane.showMessageDialog(this, "Menyiapkan laporan PDF...");
                 com.sentragizi.modules.inspector.services.PdfReportService pdfService = 
                     new com.sentragizi.modules.inspector.services.PdfReportService();
                 pdfService.generateReport(batchUuid);
             } catch (Exception ex) {
                 ex.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage());
             }
        });

        JButton btnClose = new JButton("Tutup");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dispose());

        pnlFooter.add(btnPrint);
        pnlFooter.add(btnClose);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    // --- HELPER: POPUP IMAGE VIEWER ---
    private void showImagePreview(String imagePath) {
        JDialog dialog = new JDialog(this, "Preview Foto Bukti", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(imagePath);
            // Scaling gambar agar pas di layar
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(580, 450, Image.SCALE_SMOOTH); // Fit to dialog
            
            JLabel lblImg = new JLabel(new ImageIcon(scaledImg));
            dialog.add(lblImg);
        } else {
            JLabel lblErr = new JLabel("File gambar tidak ditemukan di:\n" + imagePath, SwingConstants.CENTER);
            lblErr.setForeground(Color.RED);
            dialog.add(lblErr);
        }
        
        dialog.setVisible(true);
    }
}