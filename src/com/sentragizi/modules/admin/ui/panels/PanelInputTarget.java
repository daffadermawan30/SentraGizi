package com.sentragizi.modules.admin.ui.panels;

import com.sentragizi.modules.admin.repositories.DistributionTargetRepository;
import com.sentragizi.shared.models.DistributionTarget;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PanelInputTarget extends JPanel {

    // --- Components ---
    private JTextField txtName;
    private JTextField txtLocation;
    private JComboBox<String> cmbType;
    private JCheckBox chkActive;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnSave, btnDelete, btnReset;

    // --- Logic ---
    private DistributionTargetRepository repo = new DistributionTargetRepository();
    private int selectedId = 0;

    // --- Constants Colors ---
    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Color PRIMARY_BTN = new Color(46, 204, 113); // Emerald Green
    private final Color DANGER_BTN = new Color(231, 76, 60);   // Alizarin Red
    private final Color EDIT_BTN = new Color(52, 152, 219);    // Blue
    private final Color RESET_BTN = new Color(149, 165, 166);  // Concrete Gray

    public PanelInputTarget() {
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // --- 1. HEADER SECTION ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(BG_COLOR);
        
        JLabel lblTitle = new JLabel("Manajemen Tujuan Distribusi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        
        JLabel lblSubtitle = new JLabel("Kelola data Sekolah, Puskesmas, atau Mitra penerima program MBG.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(127, 140, 141));

        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblSubtitle, BorderLayout.SOUTH);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT SECTION (FORM + TABLE) ---
        JPanel pnlContent = new JPanel(new BorderLayout(25, 0));
        pnlContent.setOpaque(false);

        // A. Form Panel (Left)
        pnlContent.add(createFormPanel(), BorderLayout.WEST);

        // B. Table Panel (Center)
        pnlContent.add(createTablePanel(), BorderLayout.CENTER);

        add(pnlContent, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        // Shadow effect border
        pnlForm.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        pnlForm.setPreferredSize(new Dimension(350, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0); // Spacing bawah antar elemen
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; 
        gbc.weightx = 1.0;

        // Title Form
        JLabel lblFormTitle = new JLabel("Form Input / Edit");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(52, 73, 94));
        lblFormTitle.setBorder(new MatteBorder(0, 0, 2, 0, new Color(240, 240, 240)));
        gbc.insets = new Insets(0, 0, 20, 0);
        pnlForm.add(lblFormTitle, gbc);

        // Input: Nama
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        pnlForm.add(new JLabel("Nama Instansi / Sekolah"), gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        txtName = createTextField();
        pnlForm.add(txtName, gbc);

        // Input: Tipe
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        pnlForm.add(new JLabel("Jenis Instansi"), gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        cmbType = new JComboBox<>(new String[]{"SEKOLAH", "PUSKESMAS", "POSYANDU", "LAINNYA"});
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbType.setPreferredSize(new Dimension(0, 35));
        cmbType.setBackground(Color.WHITE);
        pnlForm.add(cmbType, gbc);

        // Input: Lokasi
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        pnlForm.add(new JLabel("Lokasi / Wilayah"), gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        txtLocation = createTextField();
        pnlForm.add(txtLocation, gbc);

        // Input: Checkbox Active
        gbc.gridy = 7;
        chkActive = new JCheckBox("Status Aktif");
        chkActive.setSelected(true);
        chkActive.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkActive.setBackground(Color.WHITE);
        chkActive.setFocusPainted(false);
        pnlForm.add(chkActive, gbc);

        // Buttons
        gbc.gridy = 8;
        gbc.weighty = 1.0; // Push to bottom if needed, or stick here
        gbc.fill = GridBagConstraints.BOTH;
        
        JPanel pnlBtn = new JPanel(new GridLayout(1, 3, 5, 0));
        pnlBtn.setBackground(Color.WHITE);
        
        btnDelete = createButton("Hapus", DANGER_BTN);
        btnReset = createButton("Reset", RESET_BTN);
        btnSave = createButton("Simpan", PRIMARY_BTN);

        btnDelete.addActionListener(e -> actionDelete());
        btnReset.addActionListener(e -> resetForm());
        btnSave.addActionListener(e -> actionSave());

        pnlBtn.add(btnDelete);
        pnlBtn.add(btnReset);
        pnlBtn.add(btnSave);
        
        pnlForm.add(pnlBtn, gbc);

        // Spacer Bottom
        gbc.gridy = 9;
        pnlForm.add(new JLabel(), gbc);

        return pnlForm;
    }

    private JPanel createTablePanel() {
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        model = new DefaultTableModel(new String[]{"ID", "Nama Instansi", "Tipe", "Lokasi", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(40); // Lebih lega
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(232, 240, 254)); // Light Blue selection
        table.setSelectionForeground(Color.BLACK);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        // Hide ID Column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Custom Renderer for Status (Colored Text)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setHorizontalAlignment(CENTER);
                
                if ("Aktif".equals(status)) {
                    label.setForeground(new Color(39, 174, 96)); // Green
                    if(!isSelected) label.setBackground(new Color(235, 250, 235)); // Light Green BG
                } else {
                    label.setForeground(new Color(192, 57, 43)); // Red
                    if(!isSelected) label.setBackground(new Color(253, 237, 236)); // Light Red BG
                }
                
                // Agar background color terlihat (default JLabel opaque false di table renderer)
                if (!isSelected) {
                    label.setOpaque(true); 
                } else {
                    label.setOpaque(false); // Ikut selection color table
                }
                
                return label;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) loadSelection();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        pnlTable.add(scroll, BorderLayout.CENTER);
        return pnlTable;
    }

    // --- Helper Methods UI ---
    
    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(0, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- Logic Implementation ---

    private void refreshTable() {
        model.setRowCount(0);
        // Menggunakan method getAllTargets() agar Admin melihat SEMUA data (Aktif & Non-Aktif)
        List<DistributionTarget> list = repo.getAllTargets(); 
        
        for (DistributionTarget t : list) {
            model.addRow(new Object[]{
                t.getId(), 
                t.getName(), 
                t.getType(), 
                t.getLocation(), 
                t.isActive() ? "Aktif" : "Non-Aktif"
            });
        }
    }

    private void loadSelection() {
        int row = table.getSelectedRow();
        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        cmbType.setSelectedItem(model.getValueAt(row, 2).toString());
        txtLocation.setText(model.getValueAt(row, 3).toString());
        
        // Status Checkbox
        String status = model.getValueAt(row, 4).toString();
        chkActive.setSelected("Aktif".equals(status));
        
        btnSave.setText("Update");
        btnSave.setBackground(EDIT_BTN);
        btnDelete.setEnabled(true);
        btnDelete.setBackground(DANGER_BTN); // Ensure red
    }

    private void resetForm() {
        selectedId = 0;
        txtName.setText("");
        txtLocation.setText("");
        cmbType.setSelectedIndex(0);
        chkActive.setSelected(true);
        table.clearSelection();
        
        btnSave.setText("Simpan");
        btnSave.setBackground(PRIMARY_BTN);
        btnDelete.setEnabled(false);
        btnDelete.setBackground(new Color(200, 200, 200)); // Disabled look
    }

    private void actionSave() {
        String name = txtName.getText().trim();
        String loc = txtLocation.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Instansi tidak boleh kosong!");
            return;
        }

        boolean ok;
        if (selectedId == 0) {
            ok = repo.save(name, loc, cmbType.getSelectedItem().toString());
        } else {
            ok = repo.update(selectedId, name, loc, cmbType.getSelectedItem().toString(), chkActive.isSelected());
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            refreshTable();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actionDelete() {
        int cfm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus data ini?\n\nJika target ini sudah pernah menerima distribusi,\ndisaranakan untuk mengubah Status menjadi 'Non-Aktif' saja.", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (cfm == JOptionPane.YES_OPTION) {
            if (repo.delete(selectedId)) {
                refreshTable();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data. Kemungkinan data sedang digunakan.");
            }
        }
    }
}