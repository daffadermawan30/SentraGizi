package com.sentragizi.modules.admin.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DialogDateRange extends JDialog {
    private JFormattedTextField txtStart;
    private JFormattedTextField txtEnd;
    private boolean confirmed = false;

    public DialogDateRange(Frame owner) {
        super(owner, "Pilih Periode Laporan", true);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(20, 20));

        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 20));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Default tanggal hari ini
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE); // YYYY-MM-DD

        pnlForm.add(new JLabel("Dari Tanggal (YYYY-MM-DD):"));
        txtStart = new JFormattedTextField(today);
        pnlForm.add(txtStart);

        pnlForm.add(new JLabel("Sampai Tanggal (YYYY-MM-DD):"));
        txtEnd = new JFormattedTextField(today);
        pnlForm.add(txtEnd);

        add(pnlForm, BorderLayout.CENTER);

        // Tombol
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Batal");
        JButton btnPrint = new JButton("Cetak Rekap");
        
        btnPrint.setBackground(new Color(41, 128, 185));
        btnPrint.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnPrint.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        pnlBtn.add(btnCancel);
        pnlBtn.add(btnPrint);
        add(pnlBtn, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() { return confirmed; }
    public String getStartDate() { return txtStart.getText(); }
    public String getEndDate() { return txtEnd.getText(); }
}