package com.sentragizi.modules.ai_engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FreshnessDetector {

    
    public static class InspectionResult {
        public boolean isPass;
        public String message;

        public InspectionResult(boolean isPass, String message) {
            this.isPass = isPass;
            this.message = message;
        }
    }

    public InspectionResult checkFreshness(String imagePath) {
        
        List<String> detections = runPythonScript(imagePath);
        
        
        if (detections == null || detections.isEmpty()) {
            return new InspectionResult(false, "Gagal menjalankan AI atau tidak ada objek terdeteksi.");
        }

        
        for (String item : detections) {
            
            if (item.toLowerCase().contains("busuk")) {
                return new InspectionResult(false, "GAGAL: Terdeteksi " + item);
            }
        }

        
        return new InspectionResult(true, "LOLOS: Semua bahan segar.");
    }

    
    private List<String> runPythonScript(String imagePath) {
        List<String> results = new ArrayList<>();
        try {
            
            
            ProcessBuilder pb = new ProcessBuilder(
                "python_embed/python.exe",  
                "python_embed/main_freshness.py",
                imagePath
            );
            pb.redirectErrorStream(true); 

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                
                
                
                
                line = line.trim();
                if (line.startsWith("[") && line.endsWith("]")) {
                    String content = line.substring(1, line.length() - 1); 
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