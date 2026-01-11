package com.sentragizi.modules.auth.ui;

import com.sentragizi.modules.auth.services.AuthService;
import com.sentragizi.modules.admin.ui.AdminMainFrame;
import com.sentragizi.modules.inspector.ui.InspectorMainFrame;
import com.sentragizi.shared.models.User;
import com.sentragizi.shared.utils.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    
    private final Color BG_COLOR = new Color(240, 242, 245);     
    private final Color CARD_COLOR = Color.WHITE;                
    private final Color PRIMARY_COLOR = new Color(44, 62, 80);   
    private final Color TEXT_COLOR = new Color(50, 50, 50);

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Login - SentraGizi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 550);
        setLocationRelativeTo(null); 
        
        
        JPanel pnlMain = new JPanel(new GridBagLayout()); 
        pnlMain.setBackground(BG_COLOR);
        
        
        JPanel pnlCard = new JPanel();
        pnlCard.setLayout(new BoxLayout(pnlCard, BoxLayout.Y_AXIS));
        pnlCard.setBackground(CARD_COLOR);
        pnlCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(40, 40, 40, 40) 
        ));
        pnlCard.setPreferredSize(new Dimension(350, 400));

        
        JLabel lblTitle = new JLabel("SENTRAGIZI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Sistem Kontrol Kualitas Pangan");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        
        
        JLabel lblUser = new JLabel("Username");
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(300, 40));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));

        
        JLabel lblPass = new JLabel("Password");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));

        
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        
        
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionLogin();
                }
            }
        });

        
        btnLogin = new JButton("MASUK / LOGIN");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> actionLogin());

        
        pnlCard.add(Box.createVerticalStrut(10)); 
        pnlCard.add(lblTitle);
        pnlCard.add(lblSubtitle);
        pnlCard.add(Box.createVerticalStrut(40)); 
        
        
        JPanel pnlUserLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlUserLabel.setBackground(CARD_COLOR);
        pnlUserLabel.add(lblUser);
        pnlCard.add(pnlUserLabel);
        pnlCard.add(Box.createVerticalStrut(5));
        pnlCard.add(txtUsername);
        
        pnlCard.add(Box.createVerticalStrut(15)); 
        
        JPanel pnlPassLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlPassLabel.setBackground(CARD_COLOR);
        pnlPassLabel.add(lblPass);
        pnlCard.add(pnlPassLabel);
        pnlCard.add(Box.createVerticalStrut(5));
        pnlCard.add(txtPassword);
        
        pnlCard.add(Box.createVerticalStrut(30)); 
        pnlCard.add(btnLogin);

        
        pnlMain.add(pnlCard);
        
        
        setContentPane(pnlMain);
    }

    private void actionLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();
        
        if(u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon isi Username dan Password!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        
        btnLogin.setText("Memproses...");
        btnLogin.setEnabled(false);

        
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                
                Thread.sleep(300); 
                AuthService auth = new AuthService();
                return auth.login(u, p);
            }

            @Override
            protected void done() {
                btnLogin.setText("MASUK / LOGIN");
                btnLogin.setEnabled(true);
                
                try {
                    boolean success = get();
                    if (success) {
                        handleLoginSuccess();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, 
                            "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void handleLoginSuccess() {
        User currentUser = SessionManager.getCurrentUser();
        String role = currentUser.getRole();
        
        
        
        if ("ADMIN".equalsIgnoreCase(role)) {
            new AdminMainFrame().setVisible(true);
        } else if ("INSPECTOR".equalsIgnoreCase(role)) {
            new InspectorMainFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Role tidak dikenali: " + role, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        this.dispose(); 
    }

}