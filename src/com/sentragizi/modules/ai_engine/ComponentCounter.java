package com.sentragizi.modules.ai_engine;

import com.sentragizi.infrastructure.config.AppConfig;
import com.sentragizi.shared.models.MenuComponent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ComponentCounter {

    // Class untuk menampung hasil analisis lengkap
    public static class AnalysisResult {
        public String status; // "PASS", "FAIL", "WARN"
        public List<String> missingMandatory; // Daftar item wajib yang hilang
        public List<String> extraOptional;    // Daftar item opsional yang muncul
        public String rawDebug;               // <--- FITUR BARU: Data Mentah AI
        
        public AnalysisResult() {
            this.missingMandatory = new ArrayList<>();
            this.extraOptional = new ArrayList<>();
            this.rawDebug = "Tidak ada data";
        }
    }

    // FUNGSI UTAMA YANG DIPANGGIL DARI UI
    public AnalysisResult analyzeMenu(String imagePath, List<MenuComponent> standardMenu) {
        AnalysisResult result = new AnalysisResult();

        // 1. Panggil Python
        JSONObject aiCounts = runPythonCounting(imagePath);
        
        // --- SIMPAN DATA MENTAH UNTUK DITAMPILKAN ---
        if (aiCounts != null) {
            result.rawDebug = aiCounts.toJSONString(); // Contoh: {"ayam":1, "nasi":1}
        } else {
            result.rawDebug = "Error: Output Python Null / Kosong";
            result.status = "FAIL";
            result.missingMandatory.add("System Error: AI Gagal");
            return result;
        }
        // --------------------------------------------

        // 2. Bandingkan Standard DB vs Hasil AI
        for (MenuComponent comp : standardMenu) {
            String labelDB = comp.getAiLabel(); 
            if (labelDB == null || labelDB.isEmpty()) labelDB = comp.getComponentName();
            
            boolean isDetected = isItemDetected(labelDB, aiCounts);

            if (comp.isOptional()) {
                if (isDetected) result.extraOptional.add(comp.getComponentName());
            } else {
                if (!isDetected) result.missingMandatory.add(comp.getComponentName());
            }
        }

        // 3. Tentukan Status Akhir
        if (!result.missingMandatory.isEmpty()) {
            result.status = "FAIL";
        } else if (!result.extraOptional.isEmpty()) {
            result.status = "WARN";
        } else {
            result.status = "PASS";
        }

        return result;
    }

    // --- LOGIKA PENCOCOKAN (SAMA SEPERTI SEBELUMNYA) ---
    private boolean isItemDetected(String targetLabel, JSONObject aiCounts) {
        if (targetLabel == null) return false;
        
        String targetClean = targetLabel.toLowerCase().replace(" ", "").replace("_", "").trim();
        
        for (Object key : aiCounts.keySet()) {
            String detectedRaw = key.toString();
            String detectedClean = detectedRaw.toLowerCase().replace(" ", "").replace("_", "").trim();
            
            // 1. Cek Text Biasa
            if (detectedClean.contains(targetClean) || targetClean.contains(detectedClean)) return true;
            
            // 2. Kamus Toleransi (Edamame=Buncis, Anggur=Ayam/Kentang)
            if (targetClean.contains("edamame") && detectedClean.contains("buncis")) return true; 
            if (targetClean.contains("anggur") && (detectedClean.contains("ayam") || detectedClean.contains("kentang"))) return true;
        }
        return false;
    }

    // Helper: Eksekusi Script Python
    private JSONObject runPythonCounting(String imagePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                AppConfig.PYTHON_EXEC,
                AppConfig.SCRIPT_COUNTING,
                imagePath
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{") && line.endsWith("}")) {
                    JSONParser parser = new JSONParser();
                    return (JSONObject) parser.parse(line);
                }
                output.append(line).append("\n");
            }
            System.err.println("Python Debug Output: " + output.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}