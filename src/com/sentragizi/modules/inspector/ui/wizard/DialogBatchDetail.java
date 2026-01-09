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
    private DefaultTableModel model;

    public DialogBatchDetail(JFrame parent, String batchUuid) {
        super(parent, "Detail Inspeksi Batch", true);
        setSize(1100, 600);
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
        model = repo.getDetailTableForView(batchUuid);

        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        
        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        // --- RENDERER CUSTOM (HIGHLIGHT KUNING + FORMAT OK/X) ---
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // --- 1. LOGIKA BACKGROUND (KUNING JIKA ADA CATATAN) ---
                String followUpNote = "";
                if (table.getColumnCount() > 9) {
                    Object noteObj = table.getValueAt(row, 9);
                    followUpNote = (noteObj != null) ? noteObj.toString().trim() : "";
                }
                
                if (isSelected) {
                    c.setBackground(new Color(232, 240, 254));
                } else if (!followUpNote.isEmpty()) {
                    c.setBackground(new Color(255, 252, 220)); // Kuning lembut
                } else {
                    c.setBackground(Color.WHITE);
                }

                // --- 2. LOGIKA KHUSUS KOLOM BAU(3), RASA(4), TEKSTUR(5) ---
                if (column >= 3 && column <= 5) {
                    String s = (value != null) ? value.toString() : "";
                    
                    // Cek apakah nilainya menandakan bagus (Repository mengirim "✓")
                    boolean isOk = s.contains("✓") || s.equalsIgnoreCase("true") || s.equals("1");
                    
                    if (isOk) {
                        setText("OK");
                        c.setForeground(new Color(39, 174, 96)); // Hijau
                    } else {
                        setText("X");
                        c.setForeground(new Color(192, 57, 43)); // Merah
                    }
                    
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    setHorizontalAlignment(JLabel.CENTER);
                } 
                else {
                    // Reset untuk kolom lain (selain 3,4,5) agar tidak ikut berubah
                    if (!isSelected) c.setForeground(Color.BLACK);
                    setHorizontalAlignment(JLabel.LEFT);
                    if(column == 6) setHorizontalAlignment(JLabel.CENTER); // AI Status Center
                }
                
                return c;
            }
        };

        // --- PENGATURAN LEBAR KOLOM ---
        if (table.getColumnCount() > 9) {
            table.getColumnModel().getColumn(0).setPreferredWidth(130); // Bahan
            table.getColumnModel().getColumn(1).setPreferredWidth(130); // Masakan
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Vendor
            
            // Terapkan Renderer ke Kolom 0, 1, 2
            table.getColumnModel().getColumn(0).setCellRenderer(customRenderer);
            table.getColumnModel().getColumn(1).setCellRenderer(customRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(customRenderer);

            // Kolom Kecil (Bau, Rasa, Tekstur) -> Index 3, 4, 5
            for (int i = 3; i <= 5; i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(40);
                table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
            }
            
            // Kolom AI Status (Index 6)
            table.getColumnModel().getColumn(6).setPreferredWidth(80);
            table.getColumnModel().getColumn(6).setCellRenderer(customRenderer);
            
            // Status Akhir (Index 7)
            table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    // Cek catatan untuk highlight kuning
                    String followUpNote = "";
                    if (table.getColumnCount() > 9) {
                        Object noteObj = table.getValueAt(row, 9);
                        followUpNote = (noteObj != null) ? noteObj.toString().trim() : "";
                    }
                    
                    if (!followUpNote.isEmpty() && !isSelected) {
                        c.setBackground(new Color(255, 252, 220));
                    } else if (!isSelected) {
                        c.setBackground(Color.WHITE);
                    }
                    
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
            
            // Kolom Bukti Foto (Index 8)
            TableColumn colFoto = table.getColumnModel().getColumn(8);
            colFoto.setMinWidth(100);
            colFoto.setMaxWidth(100);
            colFoto.setPreferredWidth(100);
            colFoto.setHeaderValue("Bukti Foto");
            
            colFoto.setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    // Cek catatan untuk highlight kuning
                    String followUpNote = "";
                    if (table.getColumnCount() > 9) {
                        Object noteObj = table.getValueAt(row, 9);
                        followUpNote = (noteObj != null) ? noteObj.toString().trim() : "";
                    }
                    
                    if (!followUpNote.isEmpty() && !isSelected) {
                        c.setBackground(new Color(255, 252, 220));
                    } else if (!isSelected) {
                        c.setBackground(Color.WHITE);
                    }
                    
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
            
            // --- KOLOM CATATAN (Index 9) - TAMPILKAN SEBAGAI LINK ---
            TableColumn colCatatan = table.getColumnModel().getColumn(9);
            colCatatan.setMinWidth(100);
            colCatatan.setMaxWidth(100);
            colCatatan.setPreferredWidth(100);
            colCatatan.setHeaderValue("Catatan");
            
            colCatatan.setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    String note = (value != null) ? value.toString().trim() : "";
                    
                    // Background kuning jika ada catatan
                    if (!note.isEmpty() && !isSelected) {
                        c.setBackground(new Color(255, 252, 220));
                    } else if (!isSelected) {
                        c.setBackground(Color.WHITE);
                    }
                    
                    if (!note.isEmpty()) {
                        c.setText("<html><font color='#D35400'><u>📝 Baca Catatan</u></font></html>");
                        c.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        c.setToolTipText("Klik untuk membaca catatan petugas");
                    } else {
                        c.setText("-");
                        c.setToolTipText(null);
                        c.setCursor(Cursor.getDefaultCursor());
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    return c;
                }
            });
        }
        
        // --- EVENT LISTENER: KLIK UNTUK LIHAT FOTO & CATATAN ---
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (row < 0) return;
                
                // Jika yang diklik adalah kolom 8 (Foto Path)
                if (col == 8) {
                    String path = (String) table.getValueAt(row, 8);
                    if (path != null && !path.isEmpty()) {
                        showImagePreview(path);
                    } else {
                        JOptionPane.showMessageDialog(DialogBatchDetail.this, 
                            "Tidak ada foto tersimpan.", 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                
                // Jika yang diklik adalah kolom 9 (Catatan)
                if (col == 9) {
                    Object noteObj = table.getValueAt(row, 9);
                    String note = (noteObj != null) ? noteObj.toString().trim() : "";
                    
                    if (!note.isEmpty()) {
                        showNoteDialog(row, note);
                    } else {
                        JOptionPane.showMessageDialog(DialogBatchDetail.this, 
                            "Tidak ada catatan untuk item ini.", 
                            "Info", 
                            JOptionPane.INFORMATION_MESSAGE);
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
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(580, 450, Image.SCALE_SMOOTH);
            
            JLabel lblImg = new JLabel(new ImageIcon(scaledImg));
            dialog.add(lblImg);
        } else {
            JLabel lblErr = new JLabel("<html><center>File gambar tidak ditemukan di:<br>" + imagePath + "</center></html>", SwingConstants.CENTER);
            lblErr.setForeground(Color.RED);
            dialog.add(lblErr);
        }
        
        dialog.setVisible(true);
    }
    
    // --- HELPER: POPUP CATATAN VIEWER ---
    private void showNoteDialog(int row, String note) {
        // Ambil info bahan dan masakan untuk header dialog
        String bahan = (String) table.getValueAt(row, 0);
        String masakan = (String) table.getValueAt(row, 1);
        
        JDialog dialog = new JDialog(this, "Catatan Petugas", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(255, 252, 220));
        pnlHeader.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitle = new JLabel("📝 Catatan Inspeksi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel lblSubtitle = new JLabel("<html><b>Bahan:</b> " + bahan + "<br><b>Masakan:</b> " + masakan + "</html>");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.DARK_GRAY);
        
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblSubtitle, BorderLayout.CENTER);
        
        // Content - Text Area untuk catatan
        JTextArea txtNote = new JTextArea(note);
        txtNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setEditable(false);
        txtNote.setMargin(new Insets(10, 10, 10, 10));
        txtNote.setBackground(new Color(255, 255, 255));
        
        JScrollPane scrollNote = new JScrollPane(txtNote);
        scrollNote.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        JPanel pnlContent = new JPanel(new BorderLayout());
        pnlContent.setBorder(new EmptyBorder(10, 15, 10, 15));
        pnlContent.add(scrollNote, BorderLayout.CENTER);
        
        // Footer - Button Close
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlFooter.setBorder(new EmptyBorder(5, 15, 15, 15));
        
        JButton btnClose = new JButton("Tutup");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setPreferredSize(new Dimension(100, 32));
        btnClose.addActionListener(e -> dialog.dispose());
        
        pnlFooter.add(btnClose);
        
        dialog.add(pnlHeader, BorderLayout.NORTH);
        dialog.add(pnlContent, BorderLayout.CENTER);
        dialog.add(pnlFooter, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
}