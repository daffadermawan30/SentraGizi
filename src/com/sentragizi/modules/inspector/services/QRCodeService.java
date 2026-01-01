package com.sentragizi.modules.inspector.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeService {

    /**
     * Menghasilkan kode QR dan menyimpannya sebagai file gambar.
     * * @param content Konten yang akan dikodekan (misalnya URL).
     * @param filePath Path lengkap file gambar yang akan disimpan.
     * @return True jika berhasil, false jika gagal.
     */
    public boolean generate(String content, String filePath) {
        int width = 300;
        int height = 300;
        String format = "png";

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, format, path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}