package com.sentragizi.modules.admin.ui;

import com.sentragizi.modules.admin.ui.panels.PanelInputMenu;
import com.sentragizi.modules.admin.ui.panels.PanelInputVendor;
import com.sentragizi.modules.admin.ui.panels.PanelAdminHistory;
import com.sentragizi.modules.auth.ui.LoginFrame;
import com.sentragizi.shared.utils.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminMainFrame extends JFrame {

    private JPanel pnlContent;  // Panel Tengah (Konten Berubah-ubah)
    private CardLayout cards;   // Pengatur Navigasi Halaman

    // Warna Tema Dashboard
    private final Color SIDEBAR_COLOR = new Color(44, 62, 80);    // Dark Blue
    private final Color ACTIVE_BTN_COLOR = new Color(52, 152, 219); // Blue Highlight
    private final Color HOVER_BTN_COLOR = new Color(52, 73, 94);    // Hover Effect
    private final Color TEXT_COLOR = Color.WHITE;

    public AdminMainFrame() {
        // 1. Setup Frame Utama
        setTitle("Admin Dashboard - SentraGizi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700); // Ukuran lebih lega
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 2. Setup Sidebar (Kiri)
        initSidebar();

        // 3. Setup Content (Tengah)
        initContent();
    }

    private void initSidebar() {
        JPanel pnlSidebar = new JPanel(new BorderLayout());
        pnlSidebar.setBackground(SIDEBAR_COLOR);
        pnlSidebar.setPreferredSize(new Dimension(240, 0)); // Lebar Sidebar

        // --- A. Header Sidebar (Logo/Judul) ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(SIDEBAR_COLOR);
        pnlHeader.setBorder(new EmptyBorder(30, 10, 30, 10));
        
        JLabel lblLogo = new JLabel("SENTRAGIZI");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLogo.setForeground(TEXT_COLOR);
        
        JLabel lblRole = new JLabel("Administrator Panel");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(189, 195, 199)); // Abu muda

        pnlHeader.setLayout(new GridLayout(2, 1));
        pnlHeader.add(lblLogo);
        pnlHeader.add(lblRole);
        
        pnlSidebar.add(pnlHeader, BorderLayout.NORTH);

        // --- B. Menu Buttons (Tengah) ---
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new GridLayout(5, 1, 0, 5)); // Grid vertikal dengan jarak 5px
        pnlMenu.setBackground(SIDEBAR_COLOR);
        pnlMenu.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tambahkan Tombol Menu Menggunakan Helper Method
        pnlMenu.add(createMenuButton("🍽  Kelola Menu", "cardMenu"));
        pnlMenu.add(createMenuButton("🏢  Data Vendor", "cardVendor"));
        pnlMenu.add(createMenuButton("📊  Monitoring & Laporan", "cardHistory"));
        
        // Spacer kosong agar tombol tidak terlalu renggang
        pnlMenu.add(new JLabel()); 
        
        pnlSidebar.add(pnlMenu, BorderLayout.CENTER);

        // --- C. Footer Sidebar (Logout) ---
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlFooter.setBackground(SIDEBAR_COLOR);
        pnlFooter.setBorder(new EmptyBorder(20, 10, 20, 10));

        JButton btnLogout = new JButton("Keluar / Logout");
        btnLogout.setPreferredSize(new Dimension(200, 40));
        btnLogout.setBackground(new Color(231, 76, 60)); // Merah
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBorderPainted(false);
        btnLogout.addActionListener(e -> actionLogout());

        pnlFooter.add(btnLogout);
        pnlSidebar.add(pnlFooter, BorderLayout.SOUTH);

        // Pasang Sidebar ke Frame
        add(pnlSidebar, BorderLayout.WEST);
    }

    private void initContent() {
        cards = new CardLayout();
        pnlContent = new JPanel(cards);
        pnlContent.setBackground(Color.WHITE);

        // --- PENTING: MENDAFTARKAN SEMUA PANEL ---
        pnlContent.add(new PanelInputMenu(), "cardMenu");
        pnlContent.add(new PanelInputVendor(), "cardVendor");
        pnlContent.add(new PanelAdminHistory(), "cardHistory"); // Panel Laporan Baru

        add(pnlContent, BorderLayout.CENTER);
    }

    // --- Helper Method untuk Membuat Tombol Sidebar yang Seragam ---
    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(SIDEBAR_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 15, 10, 10)); // Padding teks
        
        // Aksi saat diklik
        btn.addActionListener(e -> {
            cards.show(pnlContent, cardName);
        });

        // Efek Hover (Opsional, agar terlihat interaktif)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(HOVER_BTN_COLOR);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_COLOR);
            }
        });

        return btn;
    }

    private void actionLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin keluar dari sistem?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            this.dispose(); // Tutup window admin
            new LoginFrame().setVisible(true); // Kembali ke login
        }
    }

    public static void main(String[] args) {
        // Gunakan Look and Feel bawaan sistem agar lebih halus
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new AdminMainFrame().setVisible(true));
    }
}