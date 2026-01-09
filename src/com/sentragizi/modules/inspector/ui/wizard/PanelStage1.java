package com.sentragizi.modules.inspector.ui.wizard;

import com.sentragizi.infrastructure.config.AppConfig;
import com.sentragizi.modules.admin.repositories.MenuRepository;
import com.sentragizi.modules.admin.repositories.VendorRepository;
import com.sentragizi.modules.admin.repositories.ProductionKitchenRepository;
import com.sentragizi.modules.admin.repositories.DistributionTargetRepository;
import com.sentragizi.modules.inspector.repositories.InspectionRepository;
import com.sentragizi.shared.models.Menu;
import com.sentragizi.shared.models.MenuComponent;
import com.sentragizi.shared.models.Vendor;
import com.sentragizi.shared.models.ProductionKitchen;
import com.sentragizi.shared.models.DistributionTarget;
import com.sentragizi.shared.models.InspectionDetail;
import com.sentragizi.shared.utils.SessionManager;
import com.sentragizi.shared.models.User;
import com.sentragizi.shared.utils.FileUploader;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

public class PanelStage1 extends JPanel {

    private JComboBox<String> comboMenu;
    private JComboBox<String> comboProductionKitchen;
    private JComboBox<String> comboDistributionTarget;
    private JTable tblChecklist;
    private DefaultTableModel tableModel;
    
    // UI Components for Right Panel
    private JLabel lblImagePreview;
    private JLabel lblSelectedItem; 
    private JLabel lblDetailSubtitle;
    private JTextArea txtAiLog;
    private JTextArea txtNotes;
    private JButton btnUploadFoto;
    private JButton btnSimpan;
    private JButton btnBatal; // Tambahan Tombol Batal
    
    // Modern Colors & Fonts
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color PRIMARY_HOVER = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color SUCCESS_HOVER = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);  // Warna Merah untuk Batal
    private final Color DANGER_HOVER = new Color(192, 57, 43);
    private final Color BG_COLOR = new Color(245, 247, 250);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color TEXT_MUTED = new Color(127, 140, 141);
    
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);

    private List<Menu> menuList;
    private List<ProductionKitchen> kitchenList;
    private List<DistributionTarget> targetList;
    private String currentBatchUuid;
    private int currentMenuId = 0;
    
    private boolean isTableSelectionChanging = false;

    public PanelStage1() {
        this.currentBatchUuid = UUID.randomUUID().toString();
        initUI();
        loadMenus();
        loadProductionKitchens();
        loadDistributionTargets();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_COLOR);

        // --- HEADER SECTION ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            new EmptyBorder(15, 25, 15, 25)
        ));

        // Panel Inputs (Left Side)
        JPanel pnlInputs = new JPanel(new GridBagLayout());
        pnlInputs.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel lblMenu = createFormLabel("Target Menu");
        JLabel lblKitchen = createFormLabel("Dapur Produksi");
        JLabel lblTarget = createFormLabel("Tujuan Distribusi");

        comboMenu = createStyledComboBox();
        comboMenu.addActionListener(e -> populateTable());
        comboProductionKitchen = createStyledComboBox();
        comboDistributionTarget = createStyledComboBox();

        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0; pnlInputs.add(lblMenu, gbc);
        gbc.gridx=1; gbc.gridy=0; gbc.weightx=1; pnlInputs.add(comboMenu, gbc);
        
        gbc.gridx=2; gbc.gridy=0; gbc.weightx=0; pnlInputs.add(lblKitchen, gbc);
        gbc.gridx=3; gbc.gridy=0; gbc.weightx=1; pnlInputs.add(comboProductionKitchen, gbc);
        
        gbc.gridx=4; gbc.gridy=0; gbc.weightx=0; pnlInputs.add(lblTarget, gbc);
        gbc.gridx=5; gbc.gridy=0; gbc.weightx=1; pnlInputs.add(comboDistributionTarget, gbc);

        // Batch Badge (Right Side)
        JPanel pnlBatch = new JPanel(new BorderLayout());
        pnlBatch.setBackground(new Color(236, 240, 241));
        pnlBatch.setBorder(new LineBorder(new Color(189, 195, 199), 1, true));
        
        JLabel lblBatchTitle = new JLabel(" BATCH ID ");
        lblBatchTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBatchTitle.setForeground(TEXT_MUTED);
        lblBatchTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblBatchCode = new JLabel(" " + currentBatchUuid.substring(0, 8).toUpperCase() + " ");
        lblBatchCode.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblBatchCode.setForeground(TEXT_DARK);
        lblBatchCode.setHorizontalAlignment(SwingConstants.CENTER);
        
        pnlBatch.add(lblBatchTitle, BorderLayout.NORTH);
        pnlBatch.add(lblBatchCode, BorderLayout.CENTER);
        pnlBatch.setPreferredSize(new Dimension(100, 45));

        pnlHeader.add(pnlInputs, BorderLayout.CENTER);
        pnlHeader.add(pnlBatch, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- MAIN CONTENT (SPLIT PANE) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.65); 
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_COLOR);

        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createInspectorPanel());

        add(splitPane, BorderLayout.CENTER);
    }
    
    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }
    
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> cmb = new JComboBox<>();
        cmb.setFont(FONT_NORMAL);
        cmb.setPreferredSize(new Dimension(200, 35));
        cmb.setBackground(Color.WHITE);
        return cmb;
    }

    private JPanel createTablePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(BG_COLOR);
        pnl.setBorder(new EmptyBorder(15, 20, 20, 5));

        setupTable(); 

        JScrollPane scroll = new JScrollPane(tblChecklist);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Daftar Bahan & Parameter Kualitas");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        pnl.add(lblTitle, BorderLayout.NORTH);
        pnl.add(scroll, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createInspectorPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new CompoundBorder(
            new MatteBorder(0, 1, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; 
        
        // 1. Header Detail Section
        JPanel pnlDetailHeader = new JPanel(new BorderLayout());
        pnlDetailHeader.setBackground(Color.WHITE);
        
        lblSelectedItem = new JLabel("Pilih item di tabel...", SwingConstants.LEFT);
        lblSelectedItem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSelectedItem.setForeground(TEXT_DARK);
        
        lblDetailSubtitle = new JLabel("Detail Inspeksi & Bukti Foto");
        lblDetailSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetailSubtitle.setForeground(TEXT_MUTED);
        
        pnlDetailHeader.add(lblSelectedItem, BorderLayout.NORTH);
        pnlDetailHeader.add(lblDetailSubtitle, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        pnl.add(pnlDetailHeader, gbc);

        // 2. Image Preview Area
        lblImagePreview = new JLabel("Tidak ada foto", SwingConstants.CENTER);
        lblImagePreview.setOpaque(true);
        lblImagePreview.setBackground(new Color(245, 245, 245));
        lblImagePreview.setForeground(Color.GRAY);
        lblImagePreview.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        
        gbc.gridy = 1; gbc.weighty = 0.4;
        gbc.insets = new Insets(0, 0, 10, 0);
        pnl.add(lblImagePreview, gbc);

        // 3. Tombol Upload
        btnUploadFoto = createStyledButton("Ambil / Upload Foto & Cek AI", UIManager.getIcon("FileView.floppyDriveIcon"), PRIMARY_COLOR, PRIMARY_HOVER);
        btnUploadFoto.addActionListener(this::actionUploadAndCheckAI);
        gbc.gridy = 2; gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);
        pnl.add(btnUploadFoto, gbc);

        // 4. Log AI
        JLabel lblLogTitle = new JLabel("AI Diagnostics Log");
        lblLogTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLogTitle.setForeground(TEXT_DARK);
        
        txtAiLog = new JTextArea();
        txtAiLog.setEditable(false);
        txtAiLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtAiLog.setBackground(new Color(33, 37, 43)); 
        txtAiLog.setForeground(new Color(152, 195, 121)); 
        txtAiLog.setMargin(new Insets(5, 5, 5, 5));
        
        JScrollPane scrollLog = new JScrollPane(txtAiLog);
        scrollLog.setBorder(null);
        scrollLog.setPreferredSize(new Dimension(0, 120)); 
        
        JPanel pnlLogWrapper = new JPanel(new BorderLayout());
        pnlLogWrapper.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        pnlLogWrapper.add(lblLogTitle, BorderLayout.NORTH);
        pnlLogWrapper.add(scrollLog, BorderLayout.CENTER);
        
        gbc.gridy = 3; gbc.weighty = 0.3; 
        gbc.insets = new Insets(0, 0, 15, 0); 
        pnl.add(scrollLog, gbc);

        // 5. Catatan Input
        JLabel lblNotes = new JLabel("Catatan Tambahan (Opsional)");
        lblNotes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNotes.setForeground(TEXT_DARK);
        
        txtNotes = new JTextArea();
        txtNotes.setFont(FONT_NORMAL);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setBorder(new EmptyBorder(5, 5, 5, 5));
        txtNotes.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { syncNoteToTable(); }
            public void removeUpdate(DocumentEvent e) { syncNoteToTable(); }
            public void changedUpdate(DocumentEvent e) { syncNoteToTable(); }
        });
        
        JScrollPane scrollNote = new JScrollPane(txtNotes);
        scrollNote.setBorder(new LineBorder(new Color(200, 200, 200)));
        scrollNote.setPreferredSize(new Dimension(0, 80));
        
        JPanel pnlNoteWrapper = new JPanel(new BorderLayout(0, 5));
        pnlNoteWrapper.setBackground(Color.WHITE);
        pnlNoteWrapper.add(lblNotes, BorderLayout.NORTH);
        pnlNoteWrapper.add(scrollNote, BorderLayout.CENTER);

        gbc.gridy = 4; gbc.weighty = 0.2; 
        gbc.insets = new Insets(0, 0, 20, 0);
        pnl.add(pnlNoteWrapper, gbc);

        // 6. ACTION BUTTONS (Simpan & Batal)
        JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlButtons.setBackground(Color.WHITE);

        btnBatal = createStyledButton("BATAL / KEMBALI", null, DANGER_COLOR, DANGER_HOVER);
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBatal.addActionListener(e -> actionBatal());

        btnSimpan = createStyledButton("SIMPAN & SELESAI", null, SUCCESS_COLOR, SUCCESS_HOVER);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.addActionListener(this::actionSaveAll);
        
        pnlButtons.add(btnBatal);
        pnlButtons.add(btnSimpan);
        
        gbc.gridy = 5; gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 0, 0); 
        pnl.add(pnlButtons, gbc);

        return pnl;
    }

    private void syncNoteToTable() {
        if (isTableSelectionChanging) return; 
        int row = tblChecklist.getSelectedRow();
        if (row != -1) {
            tableModel.setValueAt(txtNotes.getText(), row, 10); 
        }
    }

    private JButton createStyledButton(String text, Icon icon, Color bg, Color hoverBg) {
        JButton btn = new JButton(text);
        if (icon != null) btn.setIcon(icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void setupTable() {
        String[] columns = {
            "Nama Masakan", "Bahan Baku", "Vendor Suplier", "Bau", "Rasa", "Tekstur", 
            "Analisa AI", "Status", "Path", "CompID", "Catatan" 
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex >= 3 && columnIndex <= 5) return Boolean.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return (col == 2) || (col >= 3 && col <= 5) || col == 7 || col == 10; 
            }
        };

        tblChecklist = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        };
        
        tblChecklist.setRowHeight(50);
        tblChecklist.setFont(FONT_NORMAL);
        tblChecklist.setShowVerticalLines(false);
        tblChecklist.setShowHorizontalLines(true);
        tblChecklist.setGridColor(new Color(230, 230, 230));
        tblChecklist.setIntercellSpacing(new Dimension(10, 0));
        tblChecklist.setSelectionBackground(new Color(232, 244, 252));
        tblChecklist.setSelectionForeground(TEXT_DARK);

        JTableHeader header = tblChecklist.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));

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
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                c.setHorizontalAlignment(CENTER);
                
                if ("PASS".equals(status)) {
                    c.setForeground(new Color(39, 174, 96)); 
                    c.setText("✔ PASS");
                } else if ("FAIL".equals(status)) {
                    c.setForeground(new Color(192, 57, 43)); 
                    c.setText("✖ FAIL");
                } else {
                    c.setForeground(Color.GRAY);
                }
                return c;
            }
        });

        // Lebar Kolom
        tblChecklist.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblChecklist.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblChecklist.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblChecklist.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblChecklist.getColumnModel().getColumn(5).setPreferredWidth(50);

        // Hide Columns
        hideColumn(0); // Nama Masakan
        hideColumn(8); // Path
        hideColumn(9); // CompID
        hideColumn(10); // Catatan

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

    private void loadProductionKitchens() {
        kitchenList = new ProductionKitchenRepository().getAllActiveKitchens();
        comboProductionKitchen.removeAllItems();
        comboProductionKitchen.addItem("- Pilih Dapur -");
        for (ProductionKitchen k : kitchenList) {
            comboProductionKitchen.addItem(k.getId() + " - " + k.getName());
        }
    }

    private void loadDistributionTargets() {
        targetList = new DistributionTargetRepository().getAllActiveTargets();
        comboDistributionTarget.removeAllItems();
        comboDistributionTarget.addItem("- Pilih Tujuan -");
        for (DistributionTarget t : targetList) {
            comboDistributionTarget.addItem(t.getId() + " - " + t.getName() + " [" + t.getType() + "]");
        }
    }

    private void populateTable() {
        int idx = comboMenu.getSelectedIndex();
        tableModel.setRowCount(0);
        lblSelectedItem.setText("Pilih item di tabel...");
        lblDetailSubtitle.setText("Detail Inspeksi & Bukti Foto");
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Tidak ada foto");
        txtAiLog.setText("");
        txtNotes.setText("");

        if (idx > 0) {
            Menu selected = menuList.get(idx - 1);
            currentMenuId = selected.getId();
            List<MenuComponent> comps = new MenuRepository().getComponentsByMenuId(currentMenuId);
            
            for (MenuComponent c : comps) {
                if (c.isNeedsRawCheck()) {
                    tableModel.addRow(new Object[]{
                        c.getComponentName(), c.getRawMaterialName(), "- Pilih -", 
                        false, false, false, "-", "FAIL", "", c.getId(), "" 
                    });
                }
            }
        }
    }

    private void updatePanelInfo(int row) {
        isTableSelectionChanging = true; 

        String itemName = tableModel.getValueAt(row, 1).toString();
        String path = tableModel.getValueAt(row, 8).toString();
        
        Object noteObj = tableModel.getValueAt(row, 10);
        txtNotes.setText(noteObj != null ? noteObj.toString() : "");
        
        lblSelectedItem.setText(itemName);
        lblDetailSubtitle.setText("Bahan Baku untuk " + tableModel.getValueAt(row, 0));
        
        File imgFile = new File(path);
        if (path != null && !path.isEmpty() && imgFile.exists()) {
            ImageIcon icon = new ImageIcon(path);
            int w = lblImagePreview.getWidth();
            int h = lblImagePreview.getHeight();
            if (w <= 0) w = 300; 
            if (h <= 0) h = 200; 
            Image img = icon.getImage().getScaledInstance(Math.min(w, 400), Math.min(h, 300), Image.SCALE_SMOOTH);
            lblImagePreview.setIcon(new ImageIcon(img));
            lblImagePreview.setText("");
        } else {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("Belum ada foto");
        }
        
        isTableSelectionChanging = false; 
    }

    private void actionUploadAndCheckAI(ActionEvent e) {
        int row = tblChecklist.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan klik baris bahan di tabel terlebih dahulu.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File sourceFile = fc.getSelectedFile();
            
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            try {
                String newFileName = FileUploader.copyFile(sourceFile.getAbsolutePath(), AppConfig.DIR_UPLOAD_RAW);
                String savedPath = AppConfig.DIR_UPLOAD_RAW + newFileName;
                
                tableModel.setValueAt(savedPath, row, 8);
                updatePanelInfo(row);
                
                runAiCheck(tableModel.getValueAt(row, 1).toString(), savedPath, row);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengupload foto: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
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
                         txtAiLog.append("> HASIL: SEGAR -> (Saran: PASS)\n");
                    } else {
                         tableModel.setValueAt("Busuk (AI)", row, 6);
                         txtAiLog.append("> HASIL: BUSUK -> (Saran: FAIL)\n");
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
    
    // --- AKSI BATAL ---
    private void actionBatal() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Batalkan inspeksi ini? Semua data input akan hilang.", 
            "Konfirmasi Pembatalan", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            resetFormState();
            
            // Navigasi Kembali ke Antrian
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout) {
                CardLayout layout = (CardLayout) parent.getLayout();
                layout.show(parent, "QUEUE"); 
            }
        }
    }
    
    // Helper Reset Form (Dipakai di Save dan Batal)
    private void resetFormState() {
        tableModel.setRowCount(0);
        comboMenu.setSelectedIndex(0);
        comboProductionKitchen.setSelectedIndex(0);
        comboDistributionTarget.setSelectedIndex(0);
        txtAiLog.setText("");
        txtNotes.setText("");
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("Tidak ada foto");
        lblSelectedItem.setText("Pilih item...");
        this.currentBatchUuid = UUID.randomUUID().toString();
    }

    private void actionSaveAll(ActionEvent e) {
        if (tblChecklist.isEditing()) {
            tblChecklist.getCellEditor().stopCellEditing();
        }

        if (currentMenuId == 0) {
            JOptionPane.showMessageDialog(this, "Pilih menu terlebih dahulu!");
            return;
        }

        if (comboProductionKitchen.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih Dapur Produksi terlebih dahulu!");
            return;
        }

        if (comboDistributionTarget.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih Tujuan Distribusi terlebih dahulu!");
            return;
        }
        
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
        
        String kitchenStr = comboProductionKitchen.getSelectedItem().toString();
        int kitchenId = Integer.parseInt(kitchenStr.split(" - ")[0]);

        String targetStr = comboDistributionTarget.getSelectedItem().toString();
        int targetId = Integer.parseInt(targetStr.split(" - ")[0]);

        InspectionRepository repo = new InspectionRepository();
        User currentUser = SessionManager.getCurrentUser();
        int userId = (currentUser != null) ? currentUser.getId() : 1; 

        long inspectionId = repo.insertInspectionHeader(currentBatchUuid, currentMenuId, userId, kitchenId, targetId); 
        
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
            
            Object noteObj = tableModel.getValueAt(i, 10);
            String note = (noteObj != null) ? noteObj.toString() : ""; 
            
            int compId = 0;
            try { compId = Integer.parseInt(tableModel.getValueAt(i, 9).toString()); } catch(Exception ex){}

            if (status.equals("PASS")) passCount++;

            InspectionDetail detail = new InspectionDetail(compId, vendorId, bau, rasa, tekstur, aiRes, status, path, note);
            if (!repo.insertInspectionDetail(inspectionId, detail)) allSaved = false;
        }
        
        String workflowStatus = (passCount == tableModel.getRowCount()) ? "COMPLETED" : "REJECTED";
        repo.updateBatchStatus(currentBatchUuid, workflowStatus);

        if (allSaved) {
            JOptionPane.showMessageDialog(this, 
                "Inspeksi Selesai!\n" +
                "Status Akhir Batch: " + workflowStatus + "\n");
            
            resetFormState(); // Gunakan helper method untuk reset

            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout) {
                CardLayout layout = (CardLayout) parent.getLayout();
                layout.show(parent, "QUEUE"); 
            }

        } else {
            JOptionPane.showMessageDialog(this, "Warning: Ada data detail gagal tersimpan.");
        }
    }
}