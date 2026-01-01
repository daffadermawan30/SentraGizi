package com.sentragizi.modules.ai_engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FreshnessDetector {

    // Class pembantu untuk hasil (Bisa dipisah file sendiri atau inner class)
    public static class InspectionResult {
        public boolean isPass;
        public String message;

        public InspectionResult(boolean isPass, String message) {
            this.isPass = isPass;
            this.message = message;
        }
    }

    public InspectionResult checkFreshness(String imagePath) {
        // 1. Panggil script Python
        List<String> detections = runPythonScript(imagePath);
        
        // 2. Cek apakah ada error saat memanggil script (list kosong/null)
        if (detections == null || detections.isEmpty()) {
            return new InspectionResult(false, "Gagal menjalankan AI atau tidak ada objek terdeteksi.");
        }

        // 3. Logika Utama: Cari kata "busuk"
        for (String item : detections) {
            // Ubah ke huruf kecil biar aman (antisipasi "Busuk" atau "BUSUK")
            if (item.toLowerCase().contains("busuk")) {
                return new InspectionResult(false, "GAGAL: Terdeteksi " + item);
            }
        }

        // 4. Jika bersih, lolos
        return new InspectionResult(true, "LOLOS: Semua bahan segar.");
    }

    // --- BAGIAN TEKNIS (ProcessBuilder) ---
    private List<String> runPythonScript(String imagePath) {
        List<String> results = new ArrayList<>();
        try {
            // PENTING: Pastikan path python dan script benar
            // Ganti 'python' dengan path full (misal: "C:\\Users\\...\\python.exe") jika perlu
            ProcessBuilder pb = new ProcessBuilder(
                "python_embed/python.exe",  // <--- Ganti "python" jadi path lengkap relatif
                "python_embed/main_freshness.py",
                imagePath
            );
            pb.redirectErrorStream(true); // Gabungkan error log ke output biasa

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                // Kita anggap Python print output JSON string misal: ["ayam_busuk", "kentang"]
                // Tapi cara paling gampang parsing manual stringnya:
                
                // Bersihkan karakter brackets [] dan tanda kutip "
                line = line.trim();
                if (line.startsWith("[") && line.endsWith("]")) {
                    String content = line.substring(1, line.length() - 1); // Hapus [ dan ]
                    String[] items = content.split(",");
                    
                    for (String item : items) {
                        String cleanItem = item.trim().replace("\"", "").replace("'", "");
                        if (!cleanItem.isEmpty()) {
                            results.add(cleanItem);
                        }
                    }
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Script Python error dengan exit code: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return results;
    }
}