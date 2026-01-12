package com.sentragizi.modules.admin.ui.panels;

import com.sentragizi.modules.admin.repositories.MenuRepository;
import com.sentragizi.shared.models.Menu;
import com.sentragizi.shared.models.MenuComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelInputMenu extends JPanel {

    private JTextField txtMenuName;
    private JComboBox<String> cmbCompName;
    private JTextField txtAiLabel;
    private JTextField txtRawName;
    private JCheckBox chkOptional;
    private JCheckBox chkRaw;

    private JTable tblComponents;
    private DefaultTableModel tableModel;

    private JButton btnAdd, btnSave, btnReset, btnList, btnDeleteRow;

    private int selectedMenuId = 0;
    private final MenuRepository repo = new MenuRepository();

    private final Map<String, String> foodToRawMap = new HashMap<>();

    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color DELETE_COLOR = new Color(231, 76, 60);

    public PanelInputMenu() {
        initDataMapping();
        initUI();
    }

    private void initDataMapping() {
        foodToRawMap.put("Nasi Putih", "Beras");
        foodToRawMap.put("Nasi Goreng", "Beras");
        foodToRawMap.put("Ayam Bakar", "Ayam");
        foodToRawMap.put("Ayam Goreng", "Ayam");
        foodToRawMap.put("Ayam Kecap", "Ayam");
        foodToRawMap.put("Pisang Cuci Mulut", "Pisang");
        foodToRawMap.put("Susu Kotak/Gelas", "Susu");
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_COLOR);

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        pnlHeader.setBackground(BG_COLOR);
        JLabel lblTitle = new JLabel("Manajemen Resep & Komponen Menu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(5);
        splitPane.setBorder(new EmptyBorder(0, 20, 20, 20));
        splitPane.setBackground(BG_COLOR);

        splitPane.setLeftComponent(createFormPanel());
        splitPane.setRightComponent(createTablePanel());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 20, 15, 20)
        ));
        pnlForm.setMinimumSize(new Dimension(300, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel lblSection1 = new JLabel("1. Informasi Menu");
        lblSection1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSection1.setForeground(ACCENT_COLOR);
        pnlForm.add(lblSection1, gbc);

        gbc.gridy++;
        pnlForm.add(new JLabel("Nama Menu Paket:"), gbc);
        gbc.gridy++;
        txtMenuName = createTextField();
        pnlForm.add(txtMenuName, gbc);

        gbc.gridy++;
        pnlForm.add(Box.createVerticalStrut(15), gbc);

        gbc.gridy++;
        JLabel lblSection2 = new JLabel("2. Tambah Komponen / Masakan");
        lblSection2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSection2.setForeground(ACCENT_COLOR);
        pnlForm.add(lblSection2, gbc);

        gbc.gridy++;
        pnlForm.add(new JLabel("Nama Komponen Masakan:"), gbc);

        gbc.gridy++;
        cmbCompName = new JComboBox<>();
        cmbCompName.addItem("- Pilih / Ketik Baru -");
        for (String food : foodToRawMap.keySet()) {
            cmbCompName.addItem(food);
        }

        cmbCompName.setEditable(true);
        cmbCompName.setPreferredSize(new Dimension(0, 35));
        cmbCompName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbCompName.setBackground(Color.WHITE);

        cmbCompName.addActionListener(e -> {
            String selectedFood = (String) cmbCompName.getSelectedItem();
            if (selectedFood != null && !selectedFood.equals("- Pilih / Ketik Baru -")) {

                String rawMaterial = foodToRawMap.get(selectedFood);

                if (rawMaterial == null) {
                    if (selectedFood.toLowerCase().contains("ayam")) {
                        rawMaterial = "Ayam";
                    } else if (selectedFood.toLowerCase().contains("nasi")) {
                        rawMaterial = "Beras";
                    } else if (selectedFood.toLowerCase().contains("pisang")) {
                        rawMaterial = "Pisang";
                    } else if (selectedFood.toLowerCase().contains("susu")) {
                        rawMaterial = "Susu";
                    } else {
                        rawMaterial = "";
                    }
                }

                txtRawName.setText(rawMaterial);

                if (!rawMaterial.isEmpty()) {
                    String aiLabel = rawMaterial.toLowerCase().replace(" ", "_");
                    txtAiLabel.setText(aiLabel);
                    chkRaw.setSelected(true);
                    txtRawName.setEnabled(true);
                } else {
                    txtAiLabel.setText("");
                    chkRaw.setSelected(false);
                    txtRawName.setEnabled(false);
                }
            }
        });

        pnlForm.add(cmbCompName, gbc);

        gbc.gridy++;
        pnlForm.add(new JLabel("Bahan Baku Utama (Cek Fisik):"), gbc);
        gbc.gridy++;
        txtRawName = createTextField();
        txtRawName.setEnabled(false);
        pnlForm.add(txtRawName, gbc);

        gbc.gridy++;
        pnlForm.add(new JLabel("Kategori Bahan (Python Local):"), gbc);
        gbc.gridy++;
        txtAiLabel = createTextField();
        pnlForm.add(txtAiLabel, gbc);

        gbc.gridy++;
        JPanel pnlChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlChecks.setBackground(Color.WHITE);
        chkRaw = new JCheckBox("Wajib Cek Kondisi Mentah?");
        chkRaw.setBackground(Color.WHITE);
        chkOptional = new JCheckBox("Opsional (Boleh Tidak Ada)");
        chkOptional.setBackground(Color.WHITE);

        chkRaw.addActionListener(e -> txtRawName.setEnabled(chkRaw.isSelected()));

        pnlChecks.add(chkRaw);
        pnlForm.add(pnlChecks, gbc);

        gbc.gridy++;
        pnlForm.add(chkOptional, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);
        btnAdd = createButton("Tambahkan ke Tabel", new Color(52, 152, 219));
        btnAdd.addActionListener(this::actionAdd);
        pnlForm.add(btnAdd, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        pnlForm.add(new JLabel(), gbc);

        return pnlForm;
    }

    private JPanel createTablePanel() {
        JPanel pnlRight = new JPanel(new BorderLayout(0, 10));
        pnlRight.setBackground(BG_COLOR);

        tableModel = new DefaultTableModel(new String[]{"Masakan", "Label AI", "Opsional?", "Cek Mentah?", "Bahan Baku"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 2 || columnIndex == 3) ? Boolean.class : String.class;
            }
        };

        tblComponents = new JTable(tableModel);
        tblComponents.setRowHeight(35);
        tblComponents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblComponents.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblComponents.setShowVerticalLines(false);
        tblComponents.setSelectionBackground(new Color(232, 240, 254));
        tblComponents.setSelectionForeground(Color.BLACK);

        tblComponents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    actionEditRow();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblComponents);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                " --- ",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);
        pnlRight.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);

        JPanel pnlTableActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTableActions.setOpaque(false);
        btnDeleteRow = createButton("Hapus Baris Terpilih", DELETE_COLOR);
        btnDeleteRow.addActionListener(this::actionDeleteRow);
        pnlTableActions.add(btnDeleteRow);

        JPanel pnlGlobalActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlGlobalActions.setOpaque(false);

        btnList = createButton("Lihat Daftar", new Color(149, 165, 166));
        btnReset = createButton("Reset", new Color(231, 76, 60));
        btnSave = createButton("Simpan Menu", new Color(46, 204, 113));
        btnSave.setPreferredSize(new Dimension(150, 40));

        btnList.addActionListener(this::actionShowList);
        btnReset.addActionListener(this::actionReset);
        btnSave.addActionListener(this::actionSaveAll);

        pnlGlobalActions.add(btnList);
        pnlGlobalActions.add(btnReset);
        pnlGlobalActions.add(btnSave);

        pnlBottom.add(pnlTableActions, BorderLayout.WEST);
        pnlBottom.add(pnlGlobalActions, BorderLayout.EAST);

        pnlRight.add(pnlBottom, BorderLayout.SOUTH);

        return pnlRight;
    }

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
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void resetForm() {
        selectedMenuId = 0;
        txtMenuName.setText("");
        resetInputComponents();
        tableModel.setRowCount(0);
        btnSave.setText("Simpan Menu Baru");
        btnSave.setBackground(new Color(46, 204, 113));
    }

    private void resetInputComponents() {
        cmbCompName.setSelectedIndex(0);
        txtAiLabel.setText("");
        txtRawName.setText("");
        txtRawName.setEnabled(false);
        chkOptional.setSelected(false);
        chkRaw.setSelected(false);
    }

    private void actionAdd(ActionEvent e) {
        String comp = "";
        Object selectedObj = cmbCompName.getSelectedItem();
        if (selectedObj != null) {
            comp = selectedObj.toString().trim();
        }

        String aiLabel = txtAiLabel.getText().trim();
        boolean isOptional = chkOptional.isSelected();
        boolean check = chkRaw.isSelected();
        String raw = txtRawName.getText().trim();

        if (comp.isEmpty() || comp.equals("- Pilih / Ketik Baru -")) {
            JOptionPane.showMessageDialog(this, "Silakan pilih atau ketik nama masakan!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (check && raw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jika 'Cek Mentah' dicentang, Nama Bahan Baku wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (aiLabel.isEmpty() && !raw.isEmpty()) {
            aiLabel = raw.toLowerCase().replace(" ", "_");
        }

        tableModel.addRow(new Object[]{comp, aiLabel, isOptional, check, raw});
        resetInputComponents();
    }

    private void actionDeleteRow(ActionEvent e) {
        int row = tblComponents.getSelectedRow();
        if (row != -1) {
            tableModel.removeRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus terlebih dahulu.");
        }
    }

    private void actionEditRow() {
        int row = tblComponents.getSelectedRow();
        if (row != -1) {

            String currentFormComp = "";
            Object selectedObj = cmbCompName.getSelectedItem();
            if (selectedObj != null) {
                currentFormComp = selectedObj.toString().trim();
            }

            if (!currentFormComp.isEmpty() && !currentFormComp.equals("- Pilih / Ketik Baru -")) {

                String formAi = txtAiLabel.getText();
                boolean formOpt = chkOptional.isSelected();
                boolean formRawCheck = chkRaw.isSelected();
                String formRawName = txtRawName.getText();

                tableModel.addRow(new Object[]{
                    currentFormComp, formAi, formOpt, formRawCheck, formRawName
                });
            }

            String comp = tableModel.getValueAt(row, 0).toString();
            String aiLabel = tableModel.getValueAt(row, 1).toString();
            boolean isOptional = (boolean) tableModel.getValueAt(row, 2);
            boolean check = (boolean) tableModel.getValueAt(row, 3);
            String raw = tableModel.getValueAt(row, 4).toString();

            cmbCompName.setSelectedItem(comp);
            if (!comp.equals(cmbCompName.getSelectedItem())) {
                cmbCompName.getEditor().setItem(comp);
            }

            txtAiLabel.setText(aiLabel);
            chkOptional.setSelected(isOptional);
            chkRaw.setSelected(check);
            txtRawName.setText(raw);
            txtRawName.setEnabled(check);

            tableModel.removeRow(row);

            btnAdd.setText("Update (Simpan Perubahan)");
            btnAdd.setBackground(ACCENT_COLOR);

            Timer t = new Timer(3000, x -> {
                btnAdd.setText("⬇ Tambahkan ke Tabel");
                btnAdd.setBackground(new Color(52, 152, 219));
            });
            t.setRepeats(false);
            t.start();
        }
    }

    private void actionSaveAll(ActionEvent e) {
        String menuName = txtMenuName.getText().trim();
        if (menuName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Menu Masakan harus diisi!");
            return;
        }
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Minimal harus ada satu komponen dalam tabel!");
            return;
        }

        List<MenuComponent> list = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            list.add(new MenuComponent(
                    tableModel.getValueAt(i, 0).toString(),
                    tableModel.getValueAt(i, 1).toString(),
                    (boolean) tableModel.getValueAt(i, 2),
                    (boolean) tableModel.getValueAt(i, 3),
                    tableModel.getValueAt(i, 4).toString()
            ));
        }

        boolean success = false;
        try {
            if (selectedMenuId == 0) {
                success = repo.saveMenuWithComponents(menuName, list);
            } else {
                if (repo.updateMenuName(selectedMenuId, menuName)) {
                    boolean deleted = repo.deleteMenuComponents(selectedMenuId);
                    if (deleted) {
                        success = repo.saveComponents(selectedMenuId, list);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Nama menu berhasil diupdate.\nTAPI Komponen menu TIDAK BISA DIUBAH karena sudah ada riwayat inspeksi.",
                                "Peringatan", JOptionPane.WARNING_MESSAGE);
                        success = true;
                    }
                }
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Proses Simpan Berhasil!");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Database: " + ex.getMessage());
        }
    }

    private void actionReset(ActionEvent e) {
        resetForm();
    }

    private void actionShowList(ActionEvent e) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Database Menu Masakan", true);
        dialog.setSize(500, 450); // Ukuran disesuaikan
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel listModel = new DefaultTableModel(new String[]{"ID", "Nama Menu Masakan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        List<Menu> menus = repo.getAllMenus();
        for (Menu m : menus) {
            listModel.addRow(new Object[]{m.getId(), m.getName()});
        }

        JTable listTable = new JTable(listModel);
        listTable.setRowHeight(35);
        listTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        listTable.getColumnModel().getColumn(0).setMinWidth(0);
        listTable.getColumnModel().getColumn(0).setMaxWidth(0);
        listTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JPanel btnPanel = new JPanel();
        JButton btnEdit = new JButton("Edit / Update");
        JButton btnDelete = new JButton("Hapus Menu");

        btnEdit.addActionListener(ev -> {
            int row = listTable.getSelectedRow();
            if (row != -1) {
                int id = Integer.parseInt(listTable.getValueAt(row, 0).toString());
                String name = listTable.getValueAt(row, 1).toString();
                loadMenuForEdit(id, name);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Pilih menu dulu!");
            }
        });

        btnDelete.addActionListener(ev -> {
            int row = listTable.getSelectedRow();
            if (row != -1) {
                int id = Integer.parseInt(listTable.getValueAt(row, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(dialog, "Hapus menu ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (repo.deleteMenu(id)) {
                        listModel.removeRow(row);
                        if (selectedMenuId == id) {
                            resetForm();
                        }
                        JOptionPane.showMessageDialog(dialog, "Menu Terhapus.");
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Gagal Hapus. Menu sudah dipakai dalam transaksi.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Pilih menu dulu!");
            }
        });

        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        dialog.add(new JScrollPane(listTable), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadMenuForEdit(int menuId, String menuName) {
        selectedMenuId = menuId;
        txtMenuName.setText(menuName);
        btnSave.setText("Update Data Menu");
        btnSave.setBackground(ACCENT_COLOR);

        List<MenuComponent> comps = repo.getComponentsByMenuId(menuId);
        tableModel.setRowCount(0);
        for (MenuComponent c : comps) {
            tableModel.addRow(new Object[]{
                c.getComponentName(),
                c.getAiLabel(),
                c.isOptional(),
                c.isNeedsRawCheck(),
                c.getRawMaterialName()
            });
        }
    }
}
