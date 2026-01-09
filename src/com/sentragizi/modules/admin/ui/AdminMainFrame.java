package com.sentragizi.modules.admin.ui;

import com.sentragizi.modules.admin.ui.panels.PanelInputMenu;
import com.sentragizi.modules.admin.ui.panels.PanelInputVendor;
import com.sentragizi.modules.admin.ui.panels.PanelAdminHistory;
import com.sentragizi.modules.admin.ui.panels.PanelInputKitchen;
import com.sentragizi.modules.admin.ui.panels.PanelInputTarget;
import com.sentragizi.modules.auth.ui.LoginFrame;
import com.sentragizi.shared.utils.SessionManager;
import com.sentragizi.shared.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class AdminMainFrame extends JFrame {

    private JPanel pnlContent;      // Container untuk CardLayout
    private CardLayout cards;       // Manager Navigasi
    private JLabel lblPageTitle;    // Judul di Header Atas
    private JLabel lblUserLogin;    // Info User di Header

    // --- Modern Color Palette (Flat UI) ---
    private final Color SIDEBAR_BG = new Color(33, 47, 61);       // Dark Blue-Black
    private final Color HEADER_BG = new Color(255, 255, 255);     // White
    private final Color CONTENT_BG = new Color(245, 247, 250);    // Light Gray

    private final Color BTN_DEFAULT = new Color(33, 47, 61);      // Sama dengan Sidebar
    private final Color BTN_HOVER = new Color(44, 62, 80);        // Sedikit lebih terang
    private final Color BTN_ACTIVE = new Color(52, 152, 219);     // Blue Accent
    private final Color TEXT_COLOR = new Color(236, 240, 241);    // Off-White

    // Menyimpan referensi tombol untuk manajemen state (Active/Inactive)
    private Map<String, SidebarButton> menuButtons = new HashMap<>();
    private String currentCard = "";

    public AdminMainFrame() {
        // 1. Setup Frame Utama
        setTitle("Admin Dashboard - SentraGizi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720); // Resolusi HD 720p standar
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Setup Components
        initSidebar();
        initMainContentArea();
    }

    // ==========================================
    // BAGIAN 1: SIDEBAR (KIRI)
    // ==========================================
    private void initSidebar() {
        JPanel pnlSidebar = new JPanel(new BorderLayout());
        pnlSidebar.setBackground(SIDEBAR_BG);
        pnlSidebar.setPreferredSize(new Dimension(260, 0)); // Lebar Sidebar

        // --- A. Header Sidebar (Brand) ---
        JPanel pnlBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        pnlBrand.setBackground(SIDEBAR_BG);
        
        JLabel lblLogo = new JLabel("<html>SENTRA<font color='#3498db'>GIZI</font></html>");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(Color.WHITE);
        
        pnlBrand.add(lblLogo);
        pnlSidebar.add(pnlBrand, BorderLayout.NORTH);

        // --- B. Menu List (Tengah) ---
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS)); // Stack vertikal
        pnlMenu.setBackground(SIDEBAR_BG);
        pnlMenu.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        pnlMenu.add(Box.createVerticalStrut(20));
        JLabel lblReports = new JLabel("   MENU");
        lblReports.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblReports.setForeground(new Color(149, 165, 166));
        lblReports.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMenu.add(lblReports);
        pnlMenu.add(Box.createVerticalStrut(10));

        // Tambahkan tombol-tombol menu
        // Format: Icon Unicode + Teks
        addMenuButton(pnlMenu, "🍽   Kelola Menu", "cardMenu");
        addMenuButton(pnlMenu, "🏢   Data Vendor", "cardVendor");
        addMenuButton(pnlMenu, "🍳   Lokasi Dapur", "cardKitchen");
        addMenuButton(pnlMenu, "🏫   Tujuan Distribusi", "cardTarget");
        
        // Spacer / Separator
        pnlMenu.add(Box.createVerticalStrut(20));
        JLabel lblReport = new JLabel("   LAPORAN & AUDIT");
        lblReport.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblReport.setForeground(new Color(149, 165, 166));
        lblReport.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMenu.add(lblReport);
        pnlMenu.add(Box.createVerticalStrut(10));
        
        addMenuButton(pnlMenu, "📊   Monitoring Inspeksi", "cardHistory");

        // Wrapper agar menu start dari atas
        JPanel pnlMenuWrapper = new JPanel(new BorderLayout());
        pnlMenuWrapper.setBackground(SIDEBAR_BG);
        pnlMenuWrapper.add(pnlMenu, BorderLayout.NORTH);
        
        pnlSidebar.add(pnlMenuWrapper, BorderLayout.CENTER);

        // --- C. Footer (Logout) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlFooter.setBackground(new Color(28, 40, 51)); // Lebih gelap dikit
        pnlFooter.setBorder(new EmptyBorder(15, 20, 15, 20));

        JButton btnLogout = new JButton("Keluar / Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(new Color(231, 76, 60)); // Merah text
        btnLogout.setBackground(null);
        btnLogout.setBorder(null);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setContentAreaFilled(false);
        btnLogout.addActionListener(e -> actionLogout());

        pnlFooter.add(btnLogout);
        pnlSidebar.add(pnlFooter, BorderLayout.SOUTH);

        add(pnlSidebar, BorderLayout.WEST);
    }

    // Helper untuk menambahkan tombol ke panel menu
    private void addMenuButton(JPanel panel, String text, String cardName) {
        SidebarButton btn = new SidebarButton(text, cardName);
        panel.add(btn);
        menuButtons.put(cardName, btn); // Simpan referensi untuk highlight nanti
    }

    // ==========================================
    // BAGIAN 2: MAIN CONTENT (TENGAH & ATAS)
    // ==========================================
    private void initMainContentArea() {
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBackground(CONTENT_BG);

        // --- A. Top Header Bar ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(HEADER_BG);
        pnlHeader.setPreferredSize(new Dimension(0, 60));
        pnlHeader.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // Judul Halaman (Kiri)
        lblPageTitle = new JLabel("Dashboard Overview");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPageTitle.setForeground(new Color(50, 50, 50));
        lblPageTitle.setBorder(new EmptyBorder(0, 25, 0, 0));
        pnlHeader.add(lblPageTitle, BorderLayout.WEST);

        // Info User (Kanan)
        User currentUser = SessionManager.getCurrentUser();
        String userName = (currentUser != null) ? currentUser.getFullname() : "Administrator";
        
        lblUserLogin = new JLabel("<html>Halo, <b>" + userName + "</b> <span style='color:gray; font-size:10px'> (Admin)</span></html>");
        lblUserLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUserLogin.setBorder(new EmptyBorder(0, 0, 0, 25));
        lblUserLogin.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Icon User sederhana (Circle)
        JLabel lblAvatar = new JLabel("👤 ");
        lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        
        JPanel pnlUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 15));
        pnlUser.setOpaque(false);
        pnlUser.add(lblAvatar);
        pnlUser.add(lblUserLogin);
        
        pnlHeader.add(pnlUser, BorderLayout.EAST);
        pnlRight.add(pnlHeader, BorderLayout.NORTH);

        // --- B. Content Card Layout ---
        cards = new CardLayout();
        pnlContent = new JPanel(cards);
        pnlContent.setBackground(CONTENT_BG);

        // Register Panels
        pnlContent.add(new PanelInputMenu(), "cardMenu");
        pnlContent.add(new PanelInputVendor(), "cardVendor");
        pnlContent.add(new PanelInputKitchen(), "cardKitchen");
        pnlContent.add(new PanelInputTarget(), "cardTarget");
        pnlContent.add(new PanelAdminHistory(), "cardHistory");

        pnlRight.add(pnlContent, BorderLayout.CENTER);
        add(pnlRight, BorderLayout.CENTER);

        // Set Default Page
        switchPage("cardMenu"); 
    }

    // ==========================================
    // LOGIC NAVIGASI & STATE
    // ==========================================
    private void switchPage(String cardName) {
        if (currentCard.equals(cardName)) return;

        // 1. Ganti Halaman
        cards.show(pnlContent, cardName);
        currentCard = cardName;

        // 2. Update Judul Header sesuai tombol
        if (menuButtons.containsKey(cardName)) {
            String rawText = menuButtons.get(cardName).getText();
            // Bersihkan icon unicode untuk judul
            String cleanTitle = rawText.replaceAll("[^a-zA-Z0-9 ]", "").trim();
            lblPageTitle.setText(cleanTitle);
        }

        // 3. Update Visual Tombol (Highlight yang aktif)
        for (Map.Entry<String, SidebarButton> entry : menuButtons.entrySet()) {
            if (entry.getKey().equals(cardName)) {
                entry.getValue().setActive(true);
            } else {
                entry.getValue().setActive(false);
            }
        }
    }

    private void actionLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin keluar dari sistem?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // ==========================================
    // INNER CLASS: CUSTOM SIDEBAR BUTTON
    // ==========================================
    private class SidebarButton extends JButton {
        private String targetCard;
        private boolean isActive = false;

        public SidebarButton(String text, String cardName) {
            super(text);
            this.targetCard = cardName;
            
            // Basic Style
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(new Color(189, 195, 199)); // Gray Text
            setBackground(SIDEBAR_BG);
            
            // Layout Settings
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(12, 25, 12, 10)); // Padding dalam
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false); // Kita gambar manual di paintComponent
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Size
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            // Action
            addActionListener(e -> switchPage(targetCard));

            // Hover Effect
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) setBackground(BTN_HOVER);
                }
                public void mouseExited(MouseEvent e) {
                    if (!isActive) setBackground(SIDEBAR_BG);
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setBackground(new Color(44, 62, 80)); // Background Active
            } else {
                setForeground(new Color(189, 195, 199));
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setBackground(SIDEBAR_BG);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Gambar Background
            if (isActive || getModel().isRollover()) {
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // Gambar Indikator Garis Kiri (Hanya jika aktif)
            if (isActive) {
                g2.setColor(BTN_ACTIVE);
                g2.fillRect(0, 0, 5, getHeight()); // Garis biru tebal 5px
            }

            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new AdminMainFrame().setVisible(true));
    }
}