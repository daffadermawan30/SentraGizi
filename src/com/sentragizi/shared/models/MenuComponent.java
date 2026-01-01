package com.sentragizi.shared.models;

public class MenuComponent {
    private int id;
    private int menuId;
    private String componentName;   // Nama di Menu (UI): "Ayam Goreng"
    
    // --- FIELD BARU (WAJIB ADA) ---
    private String aiLabel;         // Nama di Roboflow: "ayam_goreng"
    private boolean isOptional;     // Aturan: true = Boleh tidak ada (Saus/Wortel)
    // ------------------------------

    private boolean needsRawCheck;  // true = Perlu Cek Mentah (Stage 1)
    private String rawMaterialName; // Nama bahan mentah: "Daging Ayam"

    // 1. Constructor Lengkap (Dipakai oleh MenuRepository saat ambil dari DB)
    public MenuComponent(int id, int menuId, String componentName, String aiLabel, boolean isOptional, boolean needsRawCheck, String rawMaterialName) {
        this.id = id;
        this.menuId = menuId;
        this.componentName = componentName;
        this.aiLabel = aiLabel;        // <--- Baru
        this.isOptional = isOptional;  // <--- Baru
        this.needsRawCheck = needsRawCheck;
        this.rawMaterialName = rawMaterialName;
    }

    // 2. Constructor Simpel (Untuk Input Admin - Jika diperlukan)
    public MenuComponent(String componentName, String aiLabel, boolean isOptional, boolean needsRawCheck, String rawMaterialName) {
        this.componentName = componentName;
        this.aiLabel = aiLabel;
        this.isOptional = isOptional;
        this.needsRawCheck = needsRawCheck;
        this.rawMaterialName = rawMaterialName;
    }

    // --- GETTERS (PENTING) ---
    public int getId() { return id; }
    public int getMenuId() { return menuId; }
    public String getComponentName() { return componentName; }

    // Getter Baru (Akan dipanggil ComponentCounter.java)
    public String getAiLabel() { 
        // Safety check: jika null, kembalikan string kosong biar tidak error
        return aiLabel == null ? "" : aiLabel; 
    }
    
    public boolean isOptional() { return isOptional; }

    public boolean isNeedsRawCheck() { return needsRawCheck; }
    public String getRawMaterialName() { return rawMaterialName; }
}