package com.sentragizi.modules.admin.ui.panels;

import com.sentragizi.modules.admin.repositories.VendorRepository;
import com.sentragizi.shared.models.Vendor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PanelInputVendor extends JPanel {

    // --- Komponen UI ---
    private JTextField txtName;
    private JTextArea txtSpec;
    private JTable tblVendor;
    private DefaultTableModel tableModel;
    private JButton btnSimpan, btnHapus, btnReset;

    // --- Data & Logic ---
    private VendorRepository repo = new VendorRepository();
    private int selectedId = 0; // 0 = Mode Baru, >0 = Mode Edit

    public PanelInputVendor() {
        initUI();
        setupTable();
        refreshTable();
        resetForm();
    }

    // ==========================================
    // 1. SETUP TAMPILAN (UI DESIGN)
    // ==========================================
    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250)); // Background abu muda
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Margin Luar

        // --- HEADER ---
        JLabel lblTitle = new JLabel("Manajemen Data Vendor");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        add(lblTitle, BorderLayout.NORTH);

        // --- CONTENT WRAPPER (Form Kiri + Tabel Kanan) ---
        JPanel pnlContent = new JPanel(new BorderLayout(20, 0));
        pnlContent.setOpaque(false);

        // A. PANEL FORM (KIRI)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(20, 20, 20, 20)
        ));
        pnlForm.setPreferredSize(new Dimension(320, 0)); // Lebar Form Tetap

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; 
        gbc.weightx = 1.0;

        // Input: Nama Vendor
        gbc.gridy = 0;
        pnlForm.add(new JLabel("Nama Vendor:"), gbc);
        
        gbc.gridy = 1;
        txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setPreferredSize(new Dimension(0, 35));
        pnlForm.add(txtName, gbc);

        // Input: Spesialisasi
        gbc.gridy = 2;
        pnlForm.add(new JLabel("Spesialisasi / Kategori:"), gbc);
        
        gbc.gridy = 3;
        gbc.weighty = 0.5; // Biar TextArea bisa memanjang
        gbc.fill = GridBagConstraints.BOTH;
        txtSpec = new JTextArea();
        txtSpec.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSpec.setLineWrap(true);
        txtSpec.setWrapStyleWord(true);
        JScrollPane scrollSpec = new JScrollPane(txtSpec);
        pnlForm.add(scrollSpec, gbc);

        // Tombol Aksi
        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel pnlButtons = new JPanel(new GridLayout(1, 3, 5, 0)); // Grid 3 kolom
        pnlButtons.setBackground(Color.WHITE);

        btnHapus = createButton("Hapus", new Color(231, 76, 60));
        btnReset = createButton("Reset", new Color(149, 165, 166));
        btnSimpan = createButton("Simpan", new Color(46, 204, 113));

        btnHapus.addActionListener(this::actionHapus);
        btnReset.addActionListener(this::actionReset);
        btnSimpan.addActionListener(this::actionSimpan);

        pnlButtons.add(btnHapus);
        pnlButtons.add(btnReset);
        pnlButtons.add(btnSimpan);
        
        pnlForm.add(pnlButtons, gbc);
        
        // Tambahkan Spacer Kosong di bawah agar form terdorong ke atas
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        pnlForm.add(new JLabel(), gbc);

        // B. PANEL TABEL (KANAN)
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        pnlTable.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), 
                " Daftar Vendor Terdaftar", 
                TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, 
                new Font("Segoe UI", Font.BOLD, 14)
        ));

        // Setup Model Tabel Awal (Kosong)
        tableModel = new DefaultTableModel(new String[]{"ID", "No", "Nama Vendor", "Spesialisasi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblVendor = new JTable(tableModel);
        
        // Styling Tabel
        tblVendor.setRowHeight(30);
        tblVendor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblVendor.setShowVerticalLines(false);
        tblVendor.setSelectionBackground(new Color(232, 240, 254));
        tblVendor.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = tblVendor.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);

        JScrollPane scrollTable = new JScrollPane(tblVendor);
        scrollTable.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollTable.getViewport().setBackground(Color.WHITE);
        
        pnlTable.add(scrollTable, BorderLayout.CENTER);

        // GABUNGKAN
        pnlContent.add(pnlForm, BorderLayout.WEST);
        pnlContent.add(pnlTable, BorderLayout.CENTER);

        add(pnlContent, BorderLayout.CENTER);
    }

    // Helper untuk membuat tombol cantik
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(0, 35));
        return btn;
    }

    // ==========================================
    // 2. LOGIKA & FUNGSIONALITAS
    // ==========================================

    private void setupTable() {
        // Sembunyikan kolom ID (index 0)
        tblVendor.getColumnModel().getColumn(0).setMinWidth(0);
        tblVendor.getColumnModel().getColumn(0).setMaxWidth(0);
        tblVendor.getColumnModel().getColumn(0).setWidth(0);
        
        // Lebar kolom No kecil saja
        tblVendor.getColumnModel().getColumn(1).setMaxWidth(50);
        tblVendor.getColumnModel().getColumn(1).setPreferredWidth(40);

        // Event Listener: Klik Baris
        tblVendor.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblVendor.getSelectedRow() != -1) {
                fillFormFromTable();
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Vendor> list = repo.getAllVendors();
        int no = 1;
        for (Vendor v : list) {
            tableModel.addRow(new Object[]{ 
                v.getId(), 
                no++, 
                v.getName(), 
                v.getSpecialty() 
            });
        }
    }

    private void fillFormFromTable() {
        int row = tblVendor.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            txtName.setText(tableModel.getValueAt(row, 2).toString());
            txtSpec.setText(tableModel.getValueAt(row, 3).toString());

            // Ubah Mode Tombol
            btnSimpan.setText("Update");
            btnSimpan.setBackground(new Color(52, 152, 219)); // Ubah jadi Biru saat Edit
            btnHapus.setEnabled(true);
            btnReset.setEnabled(true);
        }
    }

    private void resetForm() {
        selectedId = 0;
        txtName.setText("");
        txtSpec.setText("");
        tblVendor.clearSelection();

        btnSimpan.setText("Simpan");
        btnSimpan.setBackground(new Color(46, 204, 113)); // Hijau saat Baru
        btnHapus.setEnabled(false);
        btnReset.setEnabled(true);
    }

    // --- Action Handlers ---

    private void actionSimpan(ActionEvent e) {
        String nama = txtName.getText().trim();
        String spec = txtSpec.getText().trim();

        if(nama.isEmpty() || spec.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi Nama dan Spesialisasi!");
            return;
        }

        boolean success = false;
        if (selectedId == 0) {
            // CREATE
            success = repo.saveVendor(nama, spec);
            if (success) JOptionPane.showMessageDialog(this, "Data Vendor Berhasil Disimpan!");
        } else {
            // UPDATE
            success = repo.updateVendor(selectedId, nama, spec);
            if (success) JOptionPane.showMessageDialog(this, "Data Vendor Berhasil Diperbarui!");
        }

        if (success) {
            refreshTable();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actionHapus(ActionEvent e) {
        if (selectedId == 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus vendor ini?\nData yang dihapus tidak bisa dikembalikan.", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (repo.deleteVendor(selectedId)) {
                JOptionPane.showMessageDialog(this, "Vendor Berhasil Dihapus!");
                refreshTable();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Gagal menghapus. Kemungkinan vendor ini masih dipakai di data menu.", 
                    "Gagal", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void actionReset(ActionEvent e) {
        resetForm();
    }
}