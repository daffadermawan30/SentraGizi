package com.sentragizi.modules.admin.ui.panels;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.modules.inspector.services.PdfReportService;
import com.sentragizi.modules.inspector.ui.wizard.DialogBatchDetail;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelAdminHistory extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnRefresh;

    public PanelAdminHistory() {
        initComponents();
        loadData("");
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. HEADER (Judul & Pencarian) ---
        JPanel pnlHeader = new JPanel(new BorderLayout(10, 10));
        pnlHeader.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Riwayat Inspeksi & Audit");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));

        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlControls.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari ID Batch / Menu...");
        
        btnRefresh = new JButton("Cari / Refresh");
        btnRefresh.addActionListener(e -> loadData(txtSearch.getText()));

        pnlControls.add(new JLabel("Cari:"));
        pnlControls.add(txtSearch);
        pnlControls.add(btnRefresh);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlControls, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABEL DATA ---
        // Menambahkan kolom "Alert" (Index 5)
        String[] columns = {"ID Batch", "Waktu", "Menu Masakan", "Petugas", "Status", "Alert", "Aksi", "FullUUID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; 
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        
        // Sembunyikan Full UUID (Index 7)
        table.getColumnModel().getColumn(7).setMinWidth(0);
        table.getColumnModel().getColumn(7).setMaxWidth(0);
        table.getColumnModel().getColumn(7).setWidth(0);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));

        // Renderer Status Warna (Index 4)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("COMPLETED".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(39, 174, 96));
                    setText("LOLOS");
                } else {
                    c.setForeground(Color.RED);
                    setText("DITOLAK");
                }
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return c;
            }
        });

        // Renderer Alert (Index 5) - UNTUK MENAMPILKAN TANDA PERINGATAN
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = (String) value;
                if (val != null && !val.isEmpty() && !val.equals("-")) {
                    c.setForeground(new Color(230, 126, 34)); // Orange Warning
                    c.setText("⚠️ " + val);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    c.setText("-");
                    c.setForeground(Color.LIGHT_GRAY);
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        // Event Klik: Buka Dialog Detail
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String uuid = tableModel.getValueAt(row, 7).toString(); // Ambil UUID dari kolom hidden
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(PanelAdminHistory.this);
                        new DialogBatchDetail(parentFrame, uuid).setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. FOOTER ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBackground(Color.WHITE);
        
        // Tombol 1: Cetak Batch (Per Baris)
        JButton btnPrintSelected = new JButton("Cetak Detail Batch");
        btnPrintSelected.addActionListener(e -> actionPrintSelected());
        
        // Tombol 2: Cetak Rekap (Periode) - BARU
        JButton btnPrintPeriod = new JButton("📅 Cetak Laporan Periode");
        btnPrintPeriod.setBackground(new Color(39, 174, 96)); // Hijau
        btnPrintPeriod.setForeground(Color.WHITE);
        btnPrintPeriod.addActionListener(e -> actionPrintPeriod()); // <--- Method Baru
        
        pnlFooter.add(btnPrintSelected);
        pnlFooter.add(btnPrintPeriod); // Tambahkan tombol baru
        add(pnlFooter, BorderLayout.SOUTH);
    }
    
    private void actionPrintPeriod() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        DialogDateRange dialog = new DialogDateRange(parent);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String start = dialog.getStartDate();
            String end = dialog.getEndDate();
            
            // Validasi format tanggal sederhana
            if (start.isEmpty() || end.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong!");
                return;
            }

            PdfReportService pdfService = new PdfReportService();
            pdfService.generatePeriodReport(start, end);
        }
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        
        // QUERY UPDATE: Menghitung jumlah catatan (notes) yang tidak kosong
        String sql = "SELECT i.batch_uuid, i.created_at, m.name as menu_name, u.fullname, i.workflow_status, " +
                     "(SELECT COUNT(*) FROM inspection_details d WHERE d.inspection_id = i.id AND d.follow_up_note IS NOT NULL AND d.follow_up_note != '') as note_count " +
                     "FROM inspections i " +
                     "LEFT JOIN menus m ON i.menu_id = m.id " +
                     "LEFT JOIN users u ON i.inspector_id = u.id " +
                     "WHERE i.batch_uuid LIKE ? OR m.name LIKE ? " +
                     "ORDER BY i.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = rs.getString("batch_uuid");
                String shortId = uuid.substring(0, 8) + "...";
                int noteCount = rs.getInt("note_count");
                
                // Jika ada catatan, beri label "Ada Catatan"
                String alertText = (noteCount > 0) ? "Ada Catatan" : "-";

                tableModel.addRow(new Object[]{
                    shortId,
                    rs.getString("created_at"),
                    rs.getString("menu_name"),
                    rs.getString("fullname"),
                    rs.getString("workflow_status"),
                    alertText, // Masuk ke kolom Alert
                    "Lihat Detail",
                    uuid // Hidden
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actionPrintSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dicetak laporannya.");
            return;
        }
        String uuid = tableModel.getValueAt(row, 7).toString(); // Ambil UUID (index geser jadi 7)
        
        PdfReportService pdfService = new PdfReportService();
        pdfService.generateReport(uuid);
    }
}