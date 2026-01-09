package com.sentragizi.modules.admin.ui.panels;

import com.sentragizi.modules.admin.repositories.ProductionKitchenRepository;
import com.sentragizi.shared.models.ProductionKitchen;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelInputKitchen extends JPanel {
    private JTextField txtName;
    private JTextArea txtAddress;
    private JCheckBox chkActive;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnSave, btnDelete, btnReset;
    
    private ProductionKitchenRepository repo = new ProductionKitchenRepository();
    private int selectedId = 0;

    public PanelInputKitchen() {
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JLabel lblTitle = new JLabel("Manajemen Lokasi Dapur Produksi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        add(lblTitle, BorderLayout.NORTH);

        // Content
        JPanel pnlContent = new JPanel(new BorderLayout(20, 0));
        pnlContent.setOpaque(false);

        // FORM (Kiri)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        pnlForm.setPreferredSize(new Dimension(320, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.weightx = 1.0;

        // Input Fields
        gbc.gridy = 0; pnlForm.add(new JLabel("Nama Dapur:"), gbc);
        gbc.gridy = 1; txtName = new JTextField(); pnlForm.add(txtName, gbc);
        
        gbc.gridy = 2; pnlForm.add(new JLabel("Alamat Lengkap:"), gbc);
        gbc.gridy = 3; 
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        pnlForm.add(new JScrollPane(txtAddress), gbc);
        
        gbc.gridy = 4;
        chkActive = new JCheckBox("Status Aktif?");
        chkActive.setSelected(true);
        chkActive.setBackground(Color.WHITE);
        pnlForm.add(chkActive, gbc);

        // Buttons
        gbc.gridy = 5;
        JPanel pnlBtn = new JPanel(new GridLayout(1, 3, 5, 0));
        btnDelete = new JButton("Hapus"); btnDelete.setBackground(new Color(231, 76, 60)); btnDelete.setForeground(Color.WHITE);
        btnReset = new JButton("Reset"); btnReset.setBackground(new Color(149, 165, 166)); btnReset.setForeground(Color.WHITE);
        btnSave = new JButton("Simpan"); btnSave.setBackground(new Color(46, 204, 113)); btnSave.setForeground(Color.WHITE);
        
        btnDelete.addActionListener(e -> actionDelete());
        btnReset.addActionListener(e -> resetForm());
        btnSave.addActionListener(e -> actionSave());
        
        pnlBtn.add(btnDelete); pnlBtn.add(btnReset); pnlBtn.add(btnSave);
        pnlForm.add(pnlBtn, gbc);
        
        // Spacer
        gbc.gridy = 6; gbc.weighty = 1.0; pnlForm.add(new JLabel(), gbc);

        // TABLE (Kanan)
        model = new DefaultTableModel(new String[]{"ID", "Nama Dapur", "Alamat", "Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) loadSelection();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Dapur"));
        scroll.getViewport().setBackground(Color.WHITE);

        pnlContent.add(pnlForm, BorderLayout.WEST);
        pnlContent.add(scroll, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);
        
        resetForm();
    }

    // --- PERUBAHAN DI SINI ---
    private void refreshTable() {
        model.setRowCount(0);
        // Menggunakan getAllKitchens() agar Admin melihat SEMUA data (Aktif & Non-Aktif)
        List<ProductionKitchen> list = repo.getAllKitchens(); 
        
        for (ProductionKitchen k : list) {
            model.addRow(new Object[]{
                k.getId(), 
                k.getName(), 
                k.getAddress(), 
                k.isActive() ? "Aktif" : "Non-Aktif"
            });
        }
    }
    // ------------------------

    private void loadSelection() {
        int row = table.getSelectedRow();
        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        txtAddress.setText(model.getValueAt(row, 2).toString());
        
        // Ambil status dari tabel untuk mengisi checkbox
        String status = model.getValueAt(row, 3).toString();
        chkActive.setSelected("Aktif".equals(status));
        
        btnSave.setText("Update");
        btnSave.setBackground(new Color(52, 152, 219));
        btnDelete.setEnabled(true);
    }

    private void resetForm() {
        selectedId = 0;
        txtName.setText("");
        txtAddress.setText("");
        chkActive.setSelected(true);
        table.clearSelection();
        btnSave.setText("Simpan");
        btnSave.setBackground(new Color(46, 204, 113));
        btnDelete.setEnabled(false);
    }

    private void actionSave() {
        String name = txtName.getText();
        String addr = txtAddress.getText();
        if(name.isEmpty()) return;

        boolean ok;
        if(selectedId == 0) ok = repo.save(name, addr);
        else ok = repo.update(selectedId, name, addr, chkActive.isSelected());

        if(ok) {
            JOptionPane.showMessageDialog(this, "Berhasil disimpan!");
            refreshTable();
            resetForm();
        } else JOptionPane.showMessageDialog(this, "Gagal menyimpan.");
    }

    private void actionDelete() {
        int cfm = JOptionPane.showConfirmDialog(this, "Hapus Dapur ini?\nData inspeksi lama mungkin akan error jika dihapus permanen.\nSaran: Non-Aktifkan saja.", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if(cfm == JOptionPane.YES_OPTION) {
            if(repo.delete(selectedId)) {
                refreshTable();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal hapus. Mungkin data sedang dipakai. Silakan ubah Status jadi Non-Aktif.");
            }
        }
    }
}