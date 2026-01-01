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
        String[] columns = {"ID Batch", "Waktu", "Menu Masakan", "Petugas", "Status", "Aksi", "FullUUID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; 
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);
        
        // Sembunyikan Full UUID (Index 6)
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 240));

        // Renderer Status Warna
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

        // Event Klik: Buka Dialog Detail
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String uuid = tableModel.getValueAt(row, 6).toString();
                        // Kita bisa reuse DialogBatchDetail milik Inspector karena fungsinya sama
                        // Pastikan parent frame-nya kompatibel (casting ke JFrame)
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(PanelAdminHistory.this);
                        new DialogBatchDetail(parentFrame, uuid).setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. FOOTER (Tombol Cetak Cepat) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setBackground(Color.WHITE);
        
        JButton btnPrint = new JButton("Cetak Laporan Terpilih");
        btnPrint.setBackground(new Color(52, 152, 219));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.addActionListener(e -> actionPrintSelected());
        
        pnlFooter.add(btnPrint);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        String sql = "SELECT i.batch_uuid, i.created_at, m.name as menu_name, u.fullname, i.workflow_status " +
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
                
                tableModel.addRow(new Object[]{
                    shortId,
                    rs.getString("created_at"),
                    rs.getString("menu_name"),
                    rs.getString("fullname"),
                    rs.getString("workflow_status"),
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
        String uuid = tableModel.getValueAt(row, 6).toString();
        
        // Panggil Service PDF yang sudah kita buat sebelumnya
        PdfReportService pdfService = new PdfReportService();
        pdfService.generateReport(uuid);
    }
}