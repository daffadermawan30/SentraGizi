package com.sentragizi.infrastructure.config;

import java.io.File;

public class AppConfig {

    // 1. Root Project (Folder di mana aplikasi dijalankan)
    public static final String PROJECT_ROOT = System.getProperty("user.dir");

    // 2. Path Python (Wajib ada di dalam folder project 'python_embed')
    public static final String PYTHON_EXEC = PROJECT_ROOT + "/python_embed/python.exe";
    public static final String SCRIPT_FRESHNESS = PROJECT_ROOT + "/python_embed/main_freshness.py";
    public static final String SCRIPT_COUNTING = PROJECT_ROOT + "/python_embed/main_counting.py";

    // 3. Path Penyimpanan Gambar (LOKAL PROYEK)
    // Sekarang gambar disimpan di folder: SentraGiziProject/storage/uploads/
    // Tidak lagi di C:/xampp/htdocs/
    public static final String STORAGE_ROOT = PROJECT_ROOT + "/storage/";
    
    public static final String DIR_UPLOAD_RAW = STORAGE_ROOT + "uploads/raw_materials/";
    public static final String DIR_UPLOAD_COOKED = STORAGE_ROOT + "uploads/cooked_food/";
    public static final String DIR_QR = STORAGE_ROOT + "qr_codes/"; // (Biarkan saja meski tidak dipakai)

    // Arsip (Opsional, jika mau fitur backup)
    public static final String DIR_ARCHIVE_RAW = STORAGE_ROOT + "archive/raw_materials/";
    public static final String DIR_ARCHIVE_COOKED = STORAGE_ROOT + "archive/cooked_food/";
    public static final String DIR_ARCHIVE_QR = STORAGE_ROOT + "archive/qr_codes/";

    // Blok inisialisasi untuk membuat folder otomatis jika belum ada
    static {
        createDir(DIR_UPLOAD_RAW);
        createDir(DIR_UPLOAD_COOKED);
        createDir(DIR_QR);
        createDir(DIR_ARCHIVE_RAW);
        createDir(DIR_ARCHIVE_COOKED);
        createDir(DIR_ARCHIVE_QR);
    }

    private static void createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Folder dibuat: " + path);
            }
        }
    }
}