package com.sentragizi.modules.inspector.ui.wizard;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.modules.inspector.ui.InspectorMainFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
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

public class PanelQueue extends JPanel {

    private InspectorMainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JButton btnNewInspection;

    // Constructor menerima MainFrame agar bisa navigasi ke Stage 1
    public PanelQueue(InspectorMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        loadData(); // Load data saat panel dibuka
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250)); // Warna background abu muda modern
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Margin keliling

        // --- 1. HEADER SECTION ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(null);
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Dashboard Inspeksi Bahan Baku");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        
        JLabel lblSubtitle = new JLabel("Monitoring kualitas bahan makanan dapur MBG. Klik ganda baris untuk lihat detail.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.GRAY);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubtitle);

        // Tombol Refresh
        btnRefresh = new JButton("Refresh Data");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadData());

        pnlHeader.add(pnlTitle, BorderLayout.CENTER);
        pnlHeader.add(btnRefresh, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. TABLE SECTION (CENTER) ---
        // Setup Model Tabel (Kolom 5 adalah Hidden UUID)
        String[] columns = {"ID Batch", "Waktu", "Menu Masakan", "Status", "Petugas", "FullUUID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel read-only
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40); // Baris tinggi agar lega
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);

        // Styling Header Tabel
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        // Styling Kolom Status (Warna-warni)
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        
        // Sembunyikan Kolom UUID Asli (Index 5)
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        // Listener: Double Click untuk Lihat Detail
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Klik Ganda
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String fullUuid = tableModel.getValueAt(row, 5).toString(); // Ambil UUID tersembunyi
                        // Buka Dialog Detail
                        new DialogBatchDetail(mainFrame, fullUuid).setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel pembungkus tabel
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        pnlTableCard.add(scrollPane, BorderLayout.CENTER);

        add(pnlTableCard, BorderLayout.CENTER);

        // --- 3. FOOTER / ACTION BUTTON (BOTTOM) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setOpaque(false);

        btnNewInspection = new JButton("+ Mulai Inspeksi Baru");
        btnNewInspection.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNewInspection.setBackground(new Color(41, 128, 185)); // Biru Profesional
        btnNewInspection.setForeground(Color.WHITE);
        btnNewInspection.setPreferredSize(new Dimension(220, 45));
        btnNewInspection.setFocusPainted(false);
        
        // Aksi Tombol: Pindah ke PanelStage1
        btnNewInspection.addActionListener(this::actionNewInspection);

        pnlFooter.add(btnNewInspection);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    // --- LOGIKA LOAD DATA DATABASE ---
    private void loadData() {
        tableModel.setRowCount(0); // Kosongkan tabel
        
        String sql = "SELECT i.batch_uuid, i.created_at, m.name as menu_name, i.workflow_status, u.fullname " +
                     "FROM inspections i " +
                     "LEFT JOIN menus m ON i.menu_id = m.id " +
                     "LEFT JOIN users u ON i.inspector_id = u.id " +
                     "ORDER BY i.created_at DESC LIMIT 50";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String uuid = rs.getString("batch_uuid");
                // Buat ID pendek untuk tampilan
                String shortId = (uuid != null && uuid.length() > 8) ? uuid.substring(0, 8) + "..." : uuid;
                
                tableModel.addRow(new Object[]{
                    shortId,
                    rs.getString("created_at"),
                    rs.getString("menu_name"),
                    rs.getString("workflow_status"), // COMPLETED / REJECTED
                    rs.getString("fullname"),
                    uuid // Simpan UUID penuh di kolom tersembunyi (Index 5)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data: " + e.getMessage());
        }
    }

    // --- NAVIGASI KE INPUT INSPEKSI ---
    private void actionNewInspection(ActionEvent e) {
        if (mainFrame != null) {
            mainFrame.showPage("STAGE1");
        }
    }

    // --- RENDERER KHUSUS UNTUK WARNA STATUS ---
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;
            
            if ("COMPLETED".equalsIgnoreCase(status)) {
                c.setForeground(new Color(39, 174, 96)); // Hijau
                setText("✅ LOLOS");
            } else if ("REJECTED".equalsIgnoreCase(status) || "FAIL".equalsIgnoreCase(status)) {
                c.setForeground(new Color(192, 57, 43)); // Merah
                setText("❌ DITOLAK");
            } else {
                c.setForeground(new Color(243, 156, 18)); // Oranye
                setText("⏳ PROSES");
            }
            
            if (isSelected) {
                c.setForeground(table.getSelectionForeground());
            }
            
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return c;
        }
    }
}