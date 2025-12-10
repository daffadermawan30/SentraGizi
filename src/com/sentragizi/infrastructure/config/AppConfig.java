package com.sentragizi.infrastructure.config;

import java.io.File;

public class AppConfig {

    // 1. Root Project (Untuk Python tetap di folder proyek)
    public static final String PROJECT_ROOT = System.getProperty("user.dir");

    // 2. Path Python
    public static final String PYTHON_EXEC = PROJECT_ROOT + "/python_embed/python.exe";
    public static final String SCRIPT_FRESHNESS = PROJECT_ROOT + "/python_embed/main_freshness.py";
    public static final String SCRIPT_COUNTING = PROJECT_ROOT + "/python_embed/main_counting.py";

    // --- PERUBAHAN PENTING DI SINI ---
    // Arahkan storage ke folder XAMPP agar bisa diakses Web
    // Pastikan folder "sentragizi" sudah dibuat di htdocs
    public static final String WEB_ROOT = "C:/xampp/htdocs/sentragizi/";
    public static final String DIR_ARCHIVE_RAW = PROJECT_ROOT + "/storage/archive/raw_materials/";

    public static final String DIR_UPLOAD_RAW = WEB_ROOT + "storage/uploads/raw_materials/";
    public static final String DIR_UPLOAD_COOKED = WEB_ROOT + "storage/uploads/cooked_food/";
    public static final String DIR_QR = WEB_ROOT + "storage/qr_codes/";

    // IP Address Laptopmu (Ganti dengan IP asli jika mau scan pakai HP)
    // Cara cek IP: Buka CMD -> ketik 'ipconfig' -> Lihat IPv4 Address
    public static final String BASE_URL = "http://192.168.1.4/sentragizi/";
    // Kalau cuma tes di laptop sendiri, pakai: "http://localhost/sentragizi/"
    public static final String DIR_ARCHIVE_COOKED = PROJECT_ROOT + "/storage/archive/cooked_food/";
    // >>> TAMBAHKAN INI: Path QR Lokal <<<
    public static final String DIR_ARCHIVE_QR = PROJECT_ROOT + "/storage/archive/qr_codes/";

    static {
        new File(DIR_UPLOAD_RAW).mkdirs();
        new File(DIR_UPLOAD_COOKED).mkdirs();
        new File(DIR_QR).mkdirs();

        // Membuat folder Arsip Lokal (untuk Cadangan PC)
        new File(DIR_ARCHIVE_RAW).mkdirs();
        new File(DIR_ARCHIVE_COOKED).mkdirs(); // Tambahkan baris ini
        new File(DIR_ARCHIVE_QR).mkdirs();
    }
}
