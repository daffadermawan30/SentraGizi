package com.sentragizi.modules.admin.ui.panels;

import com.sentragizi.modules.admin.repositories.ProductionKitchenRepository;
import com.sentragizi.shared.models.ProductionKitchen;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);

    public PanelInputKitchen() {
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        
        JLabel lblTitle = new JLabel("Manajemen Lokasi Dapur Produksi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_DARK);
        add(lblTitle, BorderLayout.NORTH);

        
        JPanel pnlContent = new JPanel(new BorderLayout(20, 0));
        pnlContent.setOpaque(false);

        
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        pnlForm.setPreferredSize(new Dimension(350, 0)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.weightx = 1.0;

        
        gbc.gridy = 0; pnlForm.add(createLabel("Nama Dapur:"), gbc);
        gbc.gridy = 1; 
        txtName = createStyledTextField();
        pnlForm.add(txtName, gbc);
        
        
        gbc.gridy = 2; pnlForm.add(createLabel("Alamat Lengkap:"), gbc);
        gbc.gridy = 3; 
        txtAddress = createStyledTextArea();
        JScrollPane scrollAddr = new JScrollPane(txtAddress);
        scrollAddr.setBorder(new LineBorder(new Color(200, 200, 200)));
        pnlForm.add(scrollAddr, gbc);
        
        
        gbc.gridy = 4;
        chkActive = new JCheckBox("Status Aktif / Beroperasi");
        chkActive.setFont(FONT_NORMAL);
        chkActive.setForeground(TEXT_DARK);
        chkActive.setSelected(true);
        chkActive.setBackground(Color.WHITE);
        chkActive.setFocusPainted(false);
        pnlForm.add(chkActive, gbc);

        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 0, 0); 
        JPanel pnlBtn = new JPanel(new GridLayout(1, 3, 10, 0)); 
        pnlBtn.setBackground(Color.WHITE);
        
        btnDelete = createButton("Hapus", DANGER_COLOR);
        btnReset = createButton("Reset", new Color(149, 165, 166));
        btnSave = createButton("Simpan", SUCCESS_COLOR);
        
        btnDelete.addActionListener(e -> actionDelete());
        btnReset.addActionListener(e -> resetForm());
        btnSave.addActionListener(e -> actionSave());
        
        pnlBtn.add(btnDelete); pnlBtn.add(btnReset); pnlBtn.add(btnSave);
        pnlForm.add(pnlBtn, gbc);
        
        
        gbc.gridy = 6; gbc.weighty = 1.0; pnlForm.add(new JLabel(), gbc);

        
        model = new DefaultTableModel(new String[]{"ID", "Nama Dapur", "Alamat", "Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                return c;
            }
        };
        
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(new MatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        
        table.setRowHeight(40);
        table.setFont(FONT_NORMAL);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 244, 252));
        table.setSelectionForeground(TEXT_DARK);
        
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                c.setHorizontalAlignment(CENTER);
                
                if ("Aktif".equals(status)) {
                    c.setForeground(new Color(39, 174, 96)); 
                    c.setText("● AKTIF");
                } else {
                    c.setForeground(new Color(192, 57, 43)); 
                    c.setText("● NON-AKTIF");
                }
                return c;
            }
        });
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) loadSelection();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        scroll.getViewport().setBackground(Color.WHITE);

        pnlContent.add(pnlForm, BorderLayout.WEST);
        pnlContent.add(scroll, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);
        
        resetForm();
    }

    

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 100, 100));
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField txt = new JTextField();
        txt.setFont(FONT_NORMAL);
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(8, 10, 8, 10) 
        ));
        return txt;
    }

    private JTextArea createStyledTextArea() {
        JTextArea txt = new JTextArea(3, 20);
        txt.setFont(FONT_NORMAL);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBorder(new EmptyBorder(5, 5, 5, 5));
        return txt;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 0, 10, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    

    private void refreshTable() {
        model.setRowCount(0);
        
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

    private void loadSelection() {
        int row = table.getSelectedRow();
        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        txtAddress.setText(model.getValueAt(row, 2).toString());
        
        
        String status = model.getValueAt(row, 3).toString(); 
        
        
        
        boolean isActive = status.contains("Aktif") && !status.contains("NON");
        chkActive.setSelected(isActive);
        
        btnSave.setText("Update");
        btnSave.setBackground(PRIMARY_COLOR);
        btnDelete.setEnabled(true);
    }

    private void resetForm() {
        selectedId = 0;
        txtName.setText("");
        txtAddress.setText("");
        chkActive.setSelected(true);
        table.clearSelection();
        btnSave.setText("Simpan");
        btnSave.setBackground(SUCCESS_COLOR);
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