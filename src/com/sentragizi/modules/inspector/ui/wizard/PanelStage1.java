package com.sentragizi.modules.inspector.ui.wizard;

import com.sentragizi.infrastructure.config.AppConfig;
import com.sentragizi.modules.admin.repositories.MenuRepository;
import com.sentragizi.modules.admin.repositories.VendorRepository;
import com.sentragizi.modules.inspector.repositories.InspectionRepository;
import com.sentragizi.shared.models.Menu;
import com.sentragizi.shared.models.MenuComponent;
import com.sentragizi.shared.models.Vendor;
import com.sentragizi.shared.models.InspectionDetail;
import com.sentragizi.shared.utils.SessionManager;
import com.sentragizi.shared.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

public class PanelStage1 extends JPanel {

    // --- Komponen UI ---
    private JComboBox<String> comboMenu;
    private JTable tblChecklist;
    private DefaultTableModel tableModel;
    private JLabel lblImagePreview;
    private JLabel lblSelectedItem; 
    private JTextArea txtAiLog;
    private JButton btnUploadFoto;
    private JButton btnSimpan;
    
    // --- Colors & Fonts ---
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);

    // --- Data ---
    private List<Menu> menuList;
    private String currentBatchUuid;
    private int currentMenuId = 0;

    public PanelStage1() {
        this.currentBatchUuid = UUID.randomUUID().toString();
        initUI();
        loadMenus();
    }

    // ==========================================
    // 1. SETUP TAMPILAN
    // ==========================================
    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_COLOR);

        // --- A. HEADER BAR ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Kiri: Dropdown Menu
        JPanel pnlSelection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSelection.setOpaque(false);
        JLabel lblPilih = new JLabel("Target Menu Masakan:");
        lblPilih.setFont(FONT_HEADER);
        
        comboMenu = new JComboBox<>();
        comboMenu.setPreferredSize(new Dimension(300, 35));
        comboMenu.setFont(FONT_NORMAL);
        comboMenu.addActionListener(e -> populateTable());
        
        pnlSelection.add(lblPilih);
        pnlSelection.add(comboMenu);

        // Kanan: Info Batch
        JLabel lblBatch = new JLabel("Batch: " + currentBatchUuid.substring(0, 8).toUpperCase());
        lblBatch.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblBatch.setForeground(Color.GRAY);

        pnlHeader.add(pnlSelection, BorderLayout.WEST);
        pnlHeader.add(lblBatch, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // --- B. CONTENT (SPLIT PANE) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.65); 
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createInspectorPanel());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(BG_COLOR);
        pnl.setBorder(new EmptyBorder(10, 10, 10, 5)); 

        setupTable(); 

        JTableHeader header = tblChecklist.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scroll = new JScrollPane(tblChecklist);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);

        pnl.add(scroll, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createInspectorPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 5, 0);

        lblSelectedItem = new JLabel("Pilih item di tabel...", SwingConstants.CENTER);
        lblSelectedItem.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSelectedItem.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.0;
        pnl.add(lblSelectedItem, gbc);

        lblImagePreview = new JLabel("Preview Foto", SwingConstants.CENTER);
        lblImagePreview.setOpaque(true);
        lblImagePreview.setBackground(new Color(240, 240, 240));
        lblImagePreview.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        
        gbc.gridy = 1; gbc.weighty = 1.0; 
        pnl.add(lblImagePreview, gbc);

        btnUploadFoto = createStyledButton("Upload Foto & Cek AI", UIManager.getIcon("FileView.floppyDriveIcon"), PRIMARY_COLOR);
        btnUploadFoto.addActionListener(this::actionUploadAndCheckAI);
        
        gbc.gridy = 2; gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnl.add(btnUploadFoto, gbc);

        txtAiLog = new JTextArea();
        txtAiLog.setEditable(false);
        txtAiLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtAiLog.setBackground(new Color(40, 44, 52)); 
        txtAiLog.setForeground(new Color(152, 195, 121)); 
        
        JScrollPane scrollLog = new JScrollPane(txtAiLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("AI Diagnosis Log"));
        scrollLog.setPreferredSize(new Dimension(0, 120)); 
        
        gbc.gridy = 3; 
        pnl.add(scrollLog, gbc);

        btnSimpan = createStyledButton("SIMPAN & SELESAI", null, SUCCESS_COLOR);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSimpan.setPreferredSize(new Dimension(0, 50));
        btnSimpan.addActionListener(this::actionSaveAll);

        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 0, 0); 
        pnl.add(btnSimpan, gbc);

        return pnl;
    }

    private JButton createStyledButton(String text, Icon icon, Color bg) {
        JButton btn = new JButton(text);
        if (icon != null) btn.setIcon(icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ==========================================
    // 2. LOGIC TABEL
    // ==========================================
    private void setupTable() {
        String[] columns = {
            "Nama Masakan", "Nama Bahan", "Vendor", "Bau", "Rasa", "Tekstur", 
            "Hasil AI", "Status", "Path", "CompID"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 3 && columnIndex <= 5) return Boolean.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col == 2) || (col >= 3 && col <= 5) || col == 7; 
            }
        };

        tblChecklist = new JTable(tableModel);
        tblChecklist.setRowHeight(45);
        tblChecklist.setFont(FONT_NORMAL);
        tblChecklist.setShowVerticalLines(false);
        tblChecklist.setIntercellSpacing(new Dimension(0, 0));
        tblChecklist.setSelectionBackground(new Color(225, 240, 255));
        tblChecklist.setSelectionForeground(Color.BLACK);

        tblChecklist.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblChecklist.getSelectedRow() != -1) {
                updatePanelInfo(tblChecklist.getSelectedRow());
            }
        });

        // Editors & Renderers
        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"PASS", "FAIL"});
        tblChecklist.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(comboStatus));

        JComboBox<String> comboVendor = new JComboBox<>();
        List<Vendor> vendors = new VendorRepository().getAllVendors();
        comboVendor.addItem("- Pilih -");
        for (Vendor v : vendors) comboVendor.addItem(v.getId() + " - " + v.getName());
        tblChecklist.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboVendor));

        tblChecklist.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("PASS".equals(status)) {
                    c.setForeground(new Color(0, 150, 0)); 
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else if ("FAIL".equals(status)) {
                    c.setForeground(Color.RED); 
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        hideColumn(8);
        hideColumn(9);

        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (row >= 0 && (col == 3 || col == 4 || col == 5)) {
                    validateRow(row); 
                }
            }
        });
    }
    
    private void hideColumn(int index) {
        TableColumn col = tblChecklist.getColumnModel().getColumn(index);
        col.setMinWidth(0); col.setMaxWidth(0); col.setWidth(0);
    }

    private void loadMenus() {
        menuList = new MenuRepository().getAllMenus();
        comboMenu.removeAllItems();
        comboMenu.addItem("- Pilih Menu Masakan -");
        for (Menu m : menuList) comboMenu.addItem(m.getName());
    }

    private void populateTable() {
        int idx = comboMenu.getSelectedIndex();
        tableModel.setRowCount(0);
        lblSelectedItem.setText("Pilih item di tabel...");
        lblImagePreview.setIcon(null);
        txtAiLog.setText("");

        if (idx > 0) {
            Menu selected = menuList.get(idx - 1);
            currentMenuId = selected.getId();
            List<MenuComponent> comps = new MenuRepository().getComponentsByMenuId(currentMenuId);
            
            for (MenuComponent c : comps) {
                if (c.isNeedsRawCheck()) {
                    tableModel.addRow(new Object[]{
                        c.getComponentName(), c.getRawMaterialName(), "- Pilih -", 
                        false, false, false, "-", "FAIL", "", c.getId()
                    });
                }
            }
        }
    }

    private void updatePanelInfo(int row) {
        String itemName = tableModel.getValueAt(row, 1).toString();
        String path = tableModel.getValueAt(row, 8).toString();
        
        lblSelectedItem.setText("Inspeksi: " + itemName);
        
        File imgFile = new File(path);
        if (path != null && !path.isEmpty() && imgFile.exists()) {
            ImageIcon icon = new ImageIcon(path);
            int w = lblImagePreview.getWidth();
            int h = lblImagePreview.getHeight();
            if (w <= 0) w = 300; 
            if (h <= 0) h = 200; 
            Image img = icon.getImage().getScaledInstance(Math.min(w, 300), Math.min(h, 200), Image.SCALE_SMOOTH);
            lblImagePreview.setIcon(new ImageIcon(img));
            lblImagePreview.setText("");
        } else {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("Belum ada foto");
        }
    }

    private void actionUploadAndCheckAI(ActionEvent e) {
        int row = tblChecklist.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan klik baris bahan di tabel terlebih dahulu.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            tableModel.setValueAt(file.getAbsolutePath(), row, 8);
            updatePanelInfo(row); 
            runAiCheck(tableModel.getValueAt(row, 1).toString(), file.getAbsolutePath(), row);
        }
    }

    private void runAiCheck(String itemName, String imagePath, int row) {
        txtAiLog.setText("> Memulai analisis " + itemName + "...\n> Memuat model AI...");
        String modelName = itemName.toLowerCase().replace(" ", "_");
        
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                ProcessBuilder pb = new ProcessBuilder(
                    AppConfig.PYTHON_EXEC, AppConfig.SCRIPT_FRESHNESS, modelName, imagePath
                );
                pb.redirectErrorStream(true);
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                return sb.toString();
            }

            @Override
            protected void done() {
                try {
                    String result = get().trim();
                    txtAiLog.append("> Raw Output: " + result + "\n");
                    
                    if (result.contains("Traceback") || result.contains("ModuleNotFoundError") || result.contains("Error")) {
                        tableModel.setValueAt("ERROR", row, 6);
                        txtAiLog.append("> ⚠️ SYSTEM ERROR: Script Python Bermasalah.\n");
                        validateRow(row); 
                        return;
                    }

                    boolean isFresh = result.toLowerCase().contains("pass") || 
                                      result.toLowerCase().contains("segar") || 
                                      result.toLowerCase().contains("fresh");
                    
                    if (isFresh) {
                         tableModel.setValueAt("Segar (AI)", row, 6);
                         txtAiLog.append("> ✅ HASIL: SEGAR -> (Saran: PASS)\n");
                    } else {
                         tableModel.setValueAt("Busuk (AI)", row, 6);
                         txtAiLog.append("> ❌ HASIL: BUSUK -> (Saran: FAIL)\n");
                    }
                    validateRow(row);
                } catch (Exception ex) {
                    txtAiLog.append("> ⚠️ JAVA ERROR: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void validateRow(int row) {
        boolean bau = Boolean.TRUE.equals(tableModel.getValueAt(row, 3));
        boolean rasa = Boolean.TRUE.equals(tableModel.getValueAt(row, 4));
        boolean tekstur = Boolean.TRUE.equals(tableModel.getValueAt(row, 5));
        Object aiObj = tableModel.getValueAt(row, 6);
        String aiResult = (aiObj != null) ? aiObj.toString() : "-";
        boolean aiPass = aiResult.toLowerCase().contains("segar");
        
        if (bau && rasa && tekstur && aiPass) tableModel.setValueAt("PASS", row, 7);
        else tableModel.setValueAt("FAIL", row, 7);
    }

    private void actionSaveAll(ActionEvent e) {
        if (currentMenuId == 0) return;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 2).toString().equals("- Pilih -")) {
                JOptionPane.showMessageDialog(this, "Baris " + (i+1) + ": Vendor belum dipilih!");
                return;
            }
            if (tableModel.getValueAt(i, 8).toString().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Baris " + (i+1) + ": Foto belum diupload!");
                return;
            }
        }
        
        InspectionRepository repo = new InspectionRepository();
        User currentUser = SessionManager.getCurrentUser();
        int userId = (currentUser != null) ? currentUser.getId() : 1; 

        long inspectionId = repo.insertInspectionHeader(currentBatchUuid, currentMenuId, userId); 
        
        if (inspectionId == -1) {
            JOptionPane.showMessageDialog(this, "Database Error: Gagal membuat batch.");
            return;
        }

        boolean allSaved = true;
        int passCount = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String vendorStr = tableModel.getValueAt(i, 2).toString(); 
            int vendorId = Integer.parseInt(vendorStr.split(" - ")[0]);
            
            boolean bau = Boolean.TRUE.equals(tableModel.getValueAt(i, 3));
            boolean rasa = Boolean.TRUE.equals(tableModel.getValueAt(i, 4));
            boolean tekstur = Boolean.TRUE.equals(tableModel.getValueAt(i, 5));
            String aiRes = (tableModel.getValueAt(i, 6) != null) ? tableModel.getValueAt(i, 6).toString() : "-";
            String status = tableModel.getValueAt(i, 7).toString();
            String path = tableModel.getValueAt(i, 8).toString();
            
            int compId = 0;
            try { compId = Integer.parseInt(tableModel.getValueAt(i, 9).toString()); } catch(Exception ex){}

            if (status.equals("PASS")) passCount++;

            InspectionDetail detail = new InspectionDetail(compId, vendorId, bau, rasa, tekstur, aiRes, status, path);
            if (!repo.insertInspectionDetail(inspectionId, detail)) allSaved = false;
        }
        
        String workflowStatus = (passCount == tableModel.getRowCount()) ? "COMPLETED" : "REJECTED";
        repo.updateBatchStatus(currentBatchUuid, workflowStatus);

        if (allSaved) {
            JOptionPane.showMessageDialog(this, "Inspeksi Selesai!\nStatus Akhir: " + workflowStatus);
            
            // --- RESET FORM ---
            tableModel.setRowCount(0);
            comboMenu.setSelectedIndex(0);
            txtAiLog.setText("");
            lblImagePreview.setIcon(null);
            lblSelectedItem.setText("Pilih item...");
            this.currentBatchUuid = UUID.randomUUID().toString();

            // ===============================================
            // >>> NAVIGASI PINDAH HALAMAN (OTOMATIS) <<<
            // ===============================================
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout) {
                CardLayout layout = (CardLayout) parent.getLayout();
                
                // ⚠️ PENTING: Ganti "Queue" dengan nama String yang Anda pakai 
                // saat menambahkan Panel List/Queue di MainFrame Anda.
                // Contoh di MainFrame: cards.add(new PanelInspectorList(), "Queue");
                layout.show(parent, "QUEUE"); 
            } else {
                System.out.println("Warning: Parent bukan CardLayout, tidak bisa pindah otomatis.");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Warning: Ada data detail gagal tersimpan.");
        }
    }
}