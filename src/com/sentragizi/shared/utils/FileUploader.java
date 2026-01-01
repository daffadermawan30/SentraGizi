package com.sentragizi.shared.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FileUploader {
    public static String copyFile(String sourcePath, String destinationDir) throws IOException {
        File source = new File(sourcePath);
        if (!source.exists()) {
            throw new IOException("File sumber tidak ditemukan di: " + sourcePath);
        }

        // 1. Tentukan Nama File Baru (Amankan dari konflik nama)
        // Kita gunakan BATCH-UUID dan mempertahankan ekstensi file asli
        String originalFileName = source.getName();
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        String newFileName = UUID.randomUUID().toString() + extension;
        
        File destination = new File(destinationDir + newFileName);
        
        // 2. Lakukan Proses Copy (Stream)
        try (FileInputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(destination)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
        
        return newFileName; // Kembalikan nama file baru (Contoh: "a9b1c2-d4e5.jpg")
    }
}
