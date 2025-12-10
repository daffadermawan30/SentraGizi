package com.sentragizi.modules.ai_engine;

import java.util.List;
import org.json.simple.JSONObject;

public class ComponentCounter {
    
    // Fungsi membandingkan Wajib vs Ada
    public String compareComponents(List<String> requiredItems, JSONObject aiCounts) {
        StringBuilder missingItems = new StringBuilder();
        
        for (String item : requiredItems) {
            // Logic Cerdas: Cek apakah item wajib ada di hasil hitungan AI?
            // Kita pakai "contains" biar tidak sensitif huruf besar/kecil
            boolean found = false;
            
            // Loop semua kunci yang dideteksi AI
            for (Object key : aiCounts.keySet()) {
                String detected = key.toString().toLowerCase();
                String target = item.toLowerCase();
                
                // Jika "pisang ambon" mengandung "pisang", dianggap ketemu
                if (detected.contains(target) || target.contains(detected)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                missingItems.append("- ").append(item).append(" (HILANG)\n");
            }
        }
        
        if (missingItems.length() > 0) {
            return "PERINGATAN! INDIKASI PORSI KURANG:\n" + missingItems.toString();
        } else {
            return "LENGKAP (OK)";
        }
    }
}