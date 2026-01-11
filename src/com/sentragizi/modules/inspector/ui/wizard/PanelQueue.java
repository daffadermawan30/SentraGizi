package com.sentragizi.modules.inspector.ui.wizard;

import com.sentragizi.infrastructure.database.DatabaseConnection;
import com.sentragizi.modules.inspector.ui.InspectorMainFrame;
import com.sentragizi.shared.utils.SessionManager; 
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
    private JButton btnLogout; 

    public PanelQueue(InspectorMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        loadData(); 
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        
        JPanel pnlHeader = new JPanel(new BorderLayout());
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

        
        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        
        btnRefresh = new JButton("Refresh Data");
        styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadData());

        
        btnLogout = new JButton("Logout");
        styleLogoutButton(btnLogout);
        btnLogout.addActionListener(this::actionLogout);

        pnlHeaderActions.add(btnRefresh);
        pnlHeaderActions.add(btnLogout);

        pnlHeader.add(pnlTitle, BorderLayout.CENTER);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST); 

        add(pnlHeader, BorderLayout.NORTH);

        
        String[] columns = {"ID Batch", "Waktu", "Menu Masakan", "Status", "Petugas", "FullUUID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        table.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { 
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String fullUuid = tableModel.getValueAt(row, 5).toString();
                        new DialogBatchDetail(mainFrame, fullUuid).setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel pnlTableCard = new JPanel(new BorderLayout());
        pnlTableCard.setBackground(Color.WHITE);
        pnlTableCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        pnlTableCard.add(scrollPane, BorderLayout.CENTER);

        add(pnlTableCard, BorderLayout.CENTER);

        
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlFooter.setOpaque(false);

        btnNewInspection = new JButton("+ Mulai Inspeksi Baru");
        btnNewInspection.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNewInspection.setBackground(new Color(41, 128, 185)); 
        btnNewInspection.setForeground(Color.WHITE);
        btnNewInspection.setPreferredSize(new Dimension(220, 45));
        btnNewInspection.setFocusPainted(false);
        btnNewInspection.addActionListener(this::actionNewInspection);

        pnlFooter.add(btnNewInspection);
        add(pnlFooter, BorderLayout.SOUTH);
    }

    
    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(41, 128, 185));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
    }

    private void styleLogoutButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(231, 76, 60)); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 35));
        btn.setBorder(null);
    }

    private void loadData() {
        tableModel.setRowCount(0); 
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
                String shortId = (uuid != null && uuid.length() > 8) ? uuid.substring(0, 8) + "..." : uuid;
                tableModel.addRow(new Object[]{
                    shortId, rs.getString("created_at"), rs.getString("menu_name"),
                    rs.getString("workflow_status"), rs.getString("fullname"), uuid
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionNewInspection(ActionEvent e) {
        if (mainFrame != null) mainFrame.showPage("STAGE1");
    }

    
    private void actionLogout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            
            SessionManager.logout();
            
            
            
            mainFrame.dispose(); 
            
             new com.sentragizi.modules.auth.ui.LoginFrame().setVisible(true);
        }
    }

    
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = (value != null) ? value.toString() : "";
            
            
            if ("COMPLETED".equalsIgnoreCase(status)) {
                c.setForeground(new Color(39, 174, 96)); 
                setText("LOLOS");
            
            
            } else if ("RECHECK".equalsIgnoreCase(status)) {
                c.setForeground(new Color(230, 126, 34)); 
                setText("PERIKSA ULANG");
            
            
            } else if ("REJECTED".equalsIgnoreCase(status) || "FAIL".equalsIgnoreCase(status)) {
                c.setForeground(new Color(192, 57, 43)); 
                setText("DITOLAK");
            
            
            } else {
                c.setForeground(new Color(41, 128, 185)); 
                setText("PROSES");
            }
            
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return c;
        }
    }
}