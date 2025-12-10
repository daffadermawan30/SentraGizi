package com.sentragizi.infrastructure.config;

import java.io.File;

public class AppConfig {
    
    public static final String PROJECT_ROOT = System.getProperty("user.dir");
    
    // 2. Path Python & Script
    public static final String PYTHON_EXEC = PROJECT_ROOT + "/python_embed/python.exe";
    
    // --- TAMBAHAN BARU DI SINI ---
    public static final String SCRIPT_FRESHNESS = PROJECT_ROOT + "/python_embed/main_freshness.py";
    public static final String SCRIPT_COUNTING = PROJECT_ROOT + "/python_embed/main_counting.py"; // Siapkan sekalian untuk Fase 7
    // -----------------------------
    
    // 3. Storage
    public static final String DIR_UPLOAD_RAW = PROJECT_ROOT + "/storage/uploads/raw_materials/";
    public static final String DIR_UPLOAD_COOKED = PROJECT_ROOT + "/storage/uploads/cooked_food/";
    public static final String DIR_QR = PROJECT_ROOT + "/storage/qr_codes/";
    
    static {
        new File(DIR_UPLOAD_RAW).mkdirs();
        new File(DIR_UPLOAD_COOKED).mkdirs();
        new File(DIR_QR).mkdirs();
    }
}