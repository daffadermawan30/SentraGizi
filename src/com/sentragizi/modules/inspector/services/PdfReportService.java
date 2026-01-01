package com.sentragizi.modules.inspector.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.sentragizi.modules.inspector.repositories.InspectionRepository;
import java.awt.Desktop;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.Timestamp; // <--- INI YANG SEBELUMNYA KURANG
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class PdfReportService {

    private static Font fontTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static Font fontSmall = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);

    public void generateReport(String batchUuid) {
        InspectionRepository repo = new InspectionRepository();
        ResultSet rs = repo.getReportData(batchUuid);

        if (rs == null) {
            JOptionPane.showMessageDialog(null, "Data tidak ditemukan!");
            return;
        }

        // 1. Pilih Lokasi Simpan
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        fileChooser.setSelectedFile(new java.io.File("Laporan_Inspeksi_" + batchUuid.substring(0,8) + ".pdf"));
        
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) path += ".pdf";

            Document document = new Document(PageSize.A4);
            try {
                PdfWriter.getInstance(document, new FileOutputStream(path));
                document.open();

                // --- DATA VARIABLES ---
                String menuName = "-";
                String inspectorName = "-";
                String dateStr = "-";
                String statusBatch = "-";
                
                // Buffer data detail karena ResultSet pointer bergerak
                // Kita perlu ambil header info dari baris pertama dulu
                if (rs.next()) {
                    menuName = rs.getString("menu_name");
                    inspectorName = rs.getString("inspector_name");
                    statusBatch = rs.getString("workflow_status");
                    Timestamp ts = rs.getTimestamp("created_at"); // SEKARANG PASTI AMAN
                    if (ts != null) dateStr = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(ts);
                } else {
                    JOptionPane.showMessageDialog(null, "Data detail kosong.");
                    return;
                }

                // --- HEADER PDF ---
                Paragraph title = new Paragraph("BERITA ACARA INSPEKSI BAHAN BAKU", fontTitle);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph(" ", fontNormal)); // Spasi

                // Info Batch
                PdfPTable infoTable = new PdfPTable(2);
                infoTable.setWidthPercentage(100);
                infoTable.setSpacingBefore(10f);
                infoTable.setSpacingAfter(10f);
                
                addInfoRow(infoTable, "Batch ID:", batchUuid);
                addInfoRow(infoTable, "Tanggal:", dateStr);
                addInfoRow(infoTable, "Menu Masakan:", menuName);
                addInfoRow(infoTable, "Petugas:", inspectorName);
                addInfoRow(infoTable, "Status Akhir:", statusBatch);
                
                document.add(infoTable);
                document.add(new Paragraph(" ", fontNormal));

                // --- TABEL DETAIL ITEM ---
                PdfPTable table = new PdfPTable(6); // 6 Kolom
                table.setWidthPercentage(100);
                table.setWidths(new float[]{3, 3, 1, 1, 1, 2}); // Lebar kolom proporsional

                // Header Tabel
                addTableHeader(table, "Nama Bahan");
                addTableHeader(table, "Vendor");
                addTableHeader(table, "Bau");
                addTableHeader(table, "Rasa");
                addTableHeader(table, "Tekstur");
                addTableHeader(table, "Status");

                // Isi Tabel (Looping ResultSet)
                do {
                    table.addCell(new Phrase(rs.getString("raw_material_name"), fontNormal));
                    table.addCell(new Phrase(rs.getString("vendor_name"), fontNormal));
                    
                    table.addCell(getCheckCell(rs.getBoolean("bau_ok")));
                    table.addCell(getCheckCell(rs.getBoolean("rasa_ok")));
                    table.addCell(getCheckCell(rs.getBoolean("tekstur_ok")));
                    
                    String status = rs.getString("status_final");
                    PdfPCell statusCell = new PdfPCell(new Phrase(status, fontHeader));
                    statusCell.setBackgroundColor(status.equals("PASS") ? BaseColor.GREEN : BaseColor.RED);
                    table.addCell(statusCell);
                    
                } while (rs.next());

                document.add(table);

                // --- FOOTER / TANDA TANGAN ---
                document.add(new Paragraph("\n\n"));
                PdfPTable footerTable = new PdfPTable(2);
                footerTable.setWidthPercentage(100);
                
                PdfPCell cellSign = new PdfPCell(new Paragraph("Mengetahui,\nKepala Dapur\n\n\n\n( ........................... )", fontNormal));
                cellSign.setBorder(Rectangle.NO_BORDER);
                cellSign.setHorizontalAlignment(Element.ALIGN_CENTER);
                
                PdfPCell cellInspector = new PdfPCell(new Paragraph("Petugas Inspeksi,\n\n\n\n\n( " + inspectorName + " )", fontNormal));
                cellInspector.setBorder(Rectangle.NO_BORDER);
                cellInspector.setHorizontalAlignment(Element.ALIGN_CENTER);
                
                footerTable.addCell(cellSign);
                footerTable.addCell(cellInspector);
                
                document.add(footerTable);
                
                document.add(new Paragraph("\n\nDicetak otomatis oleh Sistem SentraGizi", fontSmall));

                document.close();
                JOptionPane.showMessageDialog(null, "Laporan berhasil disimpan di:\n" + path);
                
                // Buka File Otomatis
                Desktop.getDesktop().open(new java.io.File(path));

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Gagal mencetak PDF: " + e.getMessage());
            } finally {
                // Tutup koneksi DB manual jika perlu
                try { rs.getStatement().getConnection().close(); } catch(Exception ex){}
            }
        }
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fontHeader));
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);
        
        PdfPCell c2 = new PdfPCell(new Phrase(value, fontNormal));
        c2.setBorder(Rectangle.NO_BORDER);
        table.addCell(c2);
    }

    private void addTableHeader(PdfPTable table, String title) {
        PdfPCell cell = new PdfPCell(new Phrase(title, fontHeader));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private PdfPCell getCheckCell(boolean isOk) {
        PdfPCell cell = new PdfPCell(new Phrase(isOk ? "OK" : "X", fontNormal));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }
}