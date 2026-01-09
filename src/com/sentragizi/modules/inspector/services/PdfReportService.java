package com.sentragizi.modules.inspector.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sentragizi.modules.inspector.repositories.InspectionRepository;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class PdfReportService {

    // --- 1. KONFIGURASI FONT (HIERARKI DIPERBAIKI) ---
    
    // A. FONT KOP SURAT (HEADER)
    private static final Font FONT_KOP_MAIN = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static final Font FONT_KOP_SUB = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
    private static final Font FONT_KOP_ADDR = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

    // B. FONT JUDUL DOKUMEN (BODY) - Lebih Kecil dari Kop
    private static final Font FONT_DOC_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD); 
    private static final Font FONT_DOC_SUBTITLE = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD); 

    // C. FONT ISI / TABEL
    private static final Font FONT_SECTION_HEADER = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font FONT_TABLE_HEADER = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
    private static final Font FONT_BOLD_NORMAL = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
    private static final Font FONT_SMALL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private static final Font FONT_FOOTER = new Font(Font.FontFamily.HELVETICA, 7, Font.ITALIC, BaseColor.GRAY);
    
    // --- WARNA ---
    private static final BaseColor COLOR_HEADER_BG = new BaseColor(44, 62, 80); // Dark Blue
    private static final BaseColor COLOR_SUCCESS = new BaseColor(39, 174, 96); // Hijau
    private static final BaseColor COLOR_DANGER = new BaseColor(192, 57, 43); // Merah
    private static final BaseColor COLOR_BORDER = new BaseColor(200, 200, 200);

    /**
     * GENERATE LAPORAN DETAIL (BERITA ACARA)
     */
    public void generateReport(String batchUuid) {
        InspectionRepository repo = new InspectionRepository();
        ResultSet rs = repo.getReportData(batchUuid);

        if (rs == null) {
            JOptionPane.showMessageDialog(null, "Data tidak ditemukan!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Berita Acara Inspeksi");
        fileChooser.setSelectedFile(new File("BA_Inspeksi_" + batchUuid.substring(0, 8).toUpperCase() + ".pdf"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) path += ".pdf";

            Document document = new Document(PageSize.A4);
            document.setMargins(40, 40, 30, 40);

            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
                writer.setPageEvent(new PageNumberEvent());
                
                document.open();

                // --- DATA HOLDER ---
                String menuName = "-", inspectorName = "-", kitchenName = "-", targetName = "-", dateStr = "-", statusBatch = "-";

                // Pindahkan kursor ke baris pertama untuk ambil Header Info
                if (rs.next()) {
                    menuName = checkNull(rs.getString("menu_name"));
                    inspectorName = checkNull(rs.getString("inspector_name"));
                    statusBatch = checkNull(rs.getString("workflow_status"));
                    try { kitchenName = checkNull(rs.getString("kitchen_name")); } catch(Exception e) {}
                    try { targetName = checkNull(rs.getString("target_name")); } catch(Exception e) {}

                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        dateStr = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new java.util.Locale("id", "ID")).format(ts);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Data detail kosong.");
                    return;
                }

                // 1. KOP SURAT
                addProfessionalLetterHead(document);

                // 2. JUDUL
                Paragraph title = new Paragraph("BERITA ACARA INSPEKSI MUTU", FONT_DOC_TITLE);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingBefore(10);
                document.add(title);
                
                Paragraph subtitle = new Paragraph("Nomor Batch: " + batchUuid.toUpperCase(), FONT_DOC_SUBTITLE);
                subtitle.setAlignment(Element.ALIGN_CENTER);
                subtitle.setSpacingAfter(15);
                document.add(subtitle);

                // 3. TABEL INFO (LAYOUT DIPERBAIKI: Label-Val | Label-Val)
                PdfPTable infoTable = new PdfPTable(4); 
                infoTable.setWidthPercentage(100);
                // Fix: Lebar kolom diseimbangkan agar data muat
                infoTable.setWidths(new float[]{3f, 5f, 3f, 5f}); 

                // Baris 1
                addMetadataRowSimple(infoTable, "Waktu Inspeksi", ": " + dateStr + " WIB");
                addMetadataRowSimple(infoTable, "Lokasi Dapur", ": " + kitchenName);
                
                // Baris 2
                addMetadataRowSimple(infoTable, "Menu Produksi", ": " + menuName);
                addMetadataRowSimple(infoTable, "Tujuan Distribusi", ": " + targetName);
                
                // Baris 3
                addMetadataRowSimple(infoTable, "Petugas", ": " + inspectorName);
                addMetadataRowSimple(infoTable, "Status Akhir", ": " + formatStatus(statusBatch));

                document.add(infoTable);
                document.add(new Paragraph("\n"));

                // 4. HEADER TABEL
                Paragraph detailHeader = new Paragraph("HASIL PEMERIKSAAN BAHAN BAKU", FONT_SECTION_HEADER);
                detailHeader.setSpacingAfter(5);
                document.add(detailHeader);

                // 5. TABEL UTAMA
                PdfPTable table = new PdfPTable(7);
                table.setWidthPercentage(100);
                // Lebar kolom disesuaikan untuk OK/X yang kecil
                table.setWidths(new float[]{3f, 2.5f, 1f, 1f, 1f, 1.5f, 2.5f});
                table.setHeaderRows(1);

                addTableHeader(table, "Nama Bahan");
                addTableHeader(table, "Vendor");
                addTableHeader(table, "Bau");
                addTableHeader(table, "Rasa");
                addTableHeader(table, "Tks"); 
                addTableHeader(table, "Status");
                addTableHeader(table, "Catatan");

                int rowNum = 0;
                do {
                    String material = checkNull(rs.getString("raw_material_name"));
                    String vendor = checkNull(rs.getString("vendor_name"));
                    String note = ""; 
                    try { note = checkNull(rs.getString("follow_up_note")); } catch (Exception e) {}

                    BaseColor bgColor = (rowNum % 2 == 0) ? BaseColor.WHITE : new BaseColor(250, 250, 250);

                    table.addCell(createCell(material, FONT_NORMAL, Element.ALIGN_LEFT, bgColor));
                    table.addCell(createCell(vendor, FONT_SMALL, Element.ALIGN_LEFT, bgColor));
                    
                    // FIX: Gunakan Text OK/X
                    table.addCell(createTextSymbolCell(rs.getBoolean("bau_ok"), bgColor));
                    table.addCell(createTextSymbolCell(rs.getBoolean("rasa_ok"), bgColor));
                    table.addCell(createTextSymbolCell(rs.getBoolean("tekstur_ok"), bgColor));
                    
                    table.addCell(createStatusCell(rs.getString("status_final"), bgColor));
                    table.addCell(createCell(note.isEmpty() ? "-" : note, FONT_SMALL, Element.ALIGN_LEFT, bgColor));
                    
                    rowNum++;
                } while (rs.next());

                document.add(table);

                // 6. KETERANGAN
                Paragraph legend = new Paragraph("Ket: Tks = Tekstur. Status berdasarkan pemeriksaan fisik & AI.", FONT_SMALL);
                legend.setSpacingBefore(2);
                document.add(legend);

                // 7. TANDA TANGAN
                document.add(new Paragraph("\n"));
                PdfPTable signTable = new PdfPTable(3);
                signTable.setWidthPercentage(100);
                signTable.setWidths(new float[]{1, 1, 1});

                signTable.addCell(createEmptyCell()); // Kiri Kosong
                signTable.addCell(createEmptyCell()); // Tengah Kosong

                PdfPCell signCell = new PdfPCell();
                signCell.setBorder(Rectangle.NO_BORDER);
                signCell.addElement(new Paragraph("Jakarta, " + new SimpleDateFormat("dd MMMM yyyy", new java.util.Locale("id")).format(new Date()), FONT_NORMAL));
                signCell.addElement(new Paragraph("Petugas Inspeksi,\n\n\n\n", FONT_NORMAL));
                signCell.addElement(new Paragraph("( " + inspectorName + " )", FONT_BOLD_NORMAL));
                signCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                
                signTable.addCell(signCell);
                document.add(signTable);

                // 8. FOOTER
                SimpleDateFormat sdfFooter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Paragraph footer = new Paragraph("Dicetak dari Sistem SentraGizi pada " + sdfFooter.format(new Date()), FONT_FOOTER);
                footer.setAlignment(Element.ALIGN_RIGHT);
                footer.setSpacingBefore(20);
                document.add(footer);

                document.close();
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(path));
                } else {
                    JOptionPane.showMessageDialog(null, "Laporan disimpan di: " + path);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error PDF: " + e.getMessage());
            } finally {
                try { rs.getStatement().getConnection().close(); } catch(Exception ex){}
            }
        }
    }

    /**
     * GENERATE LAPORAN REKAP PERIODE (UNTUK ADMIN)
     * (DIIMPLEMENTASIKAN KEMBALI)
     */
    public void generatePeriodReport(String startDate, String endDate) {
        Document document = new Document(PageSize.A4.rotate());
        document.setMargins(30, 30, 30, 40);
        
        try {
            String fileName = "Laporan_Rekap_" + startDate + "_sd_" + endDate + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            writer.setPageEvent(new PageNumberEvent());
            
            document.open();

            // Kop Surat
            addProfessionalLetterHead(document);

            // Judul
            Paragraph title = new Paragraph("REKAPITULASI INSPEKSI MUTU", FONT_DOC_TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph periode = new Paragraph("Periode: " + startDate + " s/d " + endDate, FONT_DOC_SUBTITLE);
            periode.setAlignment(Element.ALIGN_CENTER);
            periode.setSpacingAfter(15);
            document.add(periode);

            // Tabel Rekap
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.8f, 2f, 2f, 3f, 3f, 3f, 2f, 2.5f});

            addTableHeader(table, "No");
            addTableHeader(table, "Tanggal");
            addTableHeader(table, "Batch ID");
            addTableHeader(table, "Menu");
            addTableHeader(table, "Dapur");
            addTableHeader(table, "Tujuan");
            addTableHeader(table, "Status");
            addTableHeader(table, "Petugas");

            InspectionRepository repo = new InspectionRepository();
            ResultSet rs = repo.getReportByDateRange(startDate, endDate); 

            int totalPass = 0, totalFail = 0, rowNum = 0;

            if (rs != null) {
                while (rs.next()) {
                    rowNum++;
                    BaseColor bgColor = (rowNum % 2 == 0) ? BaseColor.WHITE : new BaseColor(250, 250, 250);
                    
                    table.addCell(createCell(String.valueOf(rowNum), FONT_NORMAL, Element.ALIGN_CENTER, bgColor));
                    
                    String dateVal = rs.getString("created_at");
                    table.addCell(createCell(dateVal.substring(0, 10), FONT_SMALL, Element.ALIGN_CENTER, bgColor));
                    
                    table.addCell(createCell(rs.getString("batch_uuid").substring(0, 8).toUpperCase(), FONT_SMALL, Element.ALIGN_CENTER, bgColor));
                    table.addCell(createCell(checkNull(rs.getString("menu_name")), FONT_NORMAL, Element.ALIGN_LEFT, bgColor));
                    
                    String kitchen = "-", target = "-";
                    try { kitchen = checkNull(rs.getString("kitchen_name")); } catch(Exception e){}
                    try { target = checkNull(rs.getString("target_name")); } catch(Exception e){}
                    
                    table.addCell(createCell(kitchen, FONT_SMALL, Element.ALIGN_LEFT, bgColor));
                    table.addCell(createCell(target, FONT_SMALL, Element.ALIGN_LEFT, bgColor));
                    
                    String status = rs.getString("workflow_status");
                    if("COMPLETED".equalsIgnoreCase(status)) totalPass++; else totalFail++;
                    
                    table.addCell(createStatusCell(status.equals("COMPLETED") ? "PASS" : "FAIL", bgColor));
                    table.addCell(createCell(checkNull(rs.getString("inspector_name")), FONT_SMALL, Element.ALIGN_LEFT, bgColor));
                }
            }
            document.add(table);

            // Summary Sederhana
            document.add(new Paragraph("\n"));
            Paragraph summary = new Paragraph("Total: " + (totalPass+totalFail) + " | Diterima: " + totalPass + " | Ditolak: " + totalFail, FONT_BOLD_NORMAL);
            summary.setAlignment(Element.ALIGN_RIGHT);
            document.add(summary);

            document.close();
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(fileName));
            } else {
                JOptionPane.showMessageDialog(null, "Rekap berhasil disimpan: " + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
        }
    }

    // ================= HELPER METHODS =================

    private void addProfessionalLetterHead(Document document) throws DocumentException {
        Paragraph company = new Paragraph("PROGRAM MAKAN BERGIZI GRATIS", FONT_KOP_MAIN);
        company.setAlignment(Element.ALIGN_CENTER);
        
        Paragraph subtitle = new Paragraph("SISTEM PENGAWASAN MUTU & GIZI (SENTRAGIZI)", FONT_KOP_SUB);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        
        Paragraph address = new Paragraph("Jl. Layanan Masyarakat No. 1, Jakarta Pusat 10110 | Telp: (021) 123-4567", FONT_KOP_ADDR);
        address.setAlignment(Element.ALIGN_CENTER);
        address.setSpacingAfter(5);

        document.add(company);
        document.add(subtitle);
        document.add(address);
        
        LineSeparator ls = new LineSeparator();
        ls.setLineWidth(1.5f);
        ls.setLineColor(BaseColor.BLACK);
        document.add(new Chunk(ls));
        document.add(new Paragraph(" ")); 
    }

    private void addMetadataRowSimple(PdfPTable table, String label, String value) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, FONT_BOLD_NORMAL));
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);
        
        PdfPCell c2 = new PdfPCell(new Phrase(value, FONT_NORMAL));
        c2.setBorder(Rectangle.NO_BORDER);
        table.addCell(c2);
    }

    private void addTableHeader(PdfPTable table, String title) {
        PdfPCell header = new PdfPCell(new Phrase(title, FONT_TABLE_HEADER));
        header.setBackgroundColor(COLOR_HEADER_BG);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPadding(5);
        header.setBorderColor(BaseColor.WHITE);
        table.addCell(header);
    }

    private PdfPCell createCell(String text, Font font, int align, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4); 
        cell.setBackgroundColor(bg);
        cell.setBorderColor(COLOR_BORDER);
        return cell;
    }

    // FIX: Menggunakan Teks OK / X (Bukan Simbol Checklist)
    private PdfPCell createTextSymbolCell(boolean ok, BaseColor bg) {
        String s = ok ? "OK" : "X";
        Font f = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, ok ? COLOR_SUCCESS : COLOR_DANGER);
        PdfPCell cell = new PdfPCell(new Phrase(s, f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(COLOR_BORDER);
        return cell;
    }

    private PdfPCell createStatusCell(String status, BaseColor bg) {
        boolean pass = "PASS".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status);
        Font f = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, pass ? COLOR_SUCCESS : COLOR_DANGER);
        PdfPCell cell = new PdfPCell(new Phrase(pass ? "PASS" : "FAIL", f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(COLOR_BORDER);
        return cell;
    }

    private PdfPCell createEmptyCell() {
        PdfPCell cell = new PdfPCell(new Phrase(""));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private String formatStatus(String status) {
        if ("COMPLETED".equalsIgnoreCase(status)) return "DITERIMA";
        if ("REJECTED".equalsIgnoreCase(status)) return "DITOLAK";
        return status;
    }

    private String checkNull(String val) {
        return (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("null")) ? "-" : val;
    }

    class PageNumberEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase phrase = new Phrase(String.format("Hal %d", writer.getPageNumber()), FONT_FOOTER);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, phrase,
                (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
        }
    }
}