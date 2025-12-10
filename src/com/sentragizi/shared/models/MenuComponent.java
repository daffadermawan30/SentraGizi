package com.sentragizi.shared.models;

public class MenuComponent {
    private int id;
    private int menuId;
    private String componentName;   // "Pisang"
    private boolean needsRawCheck;  // true = Cek Mentah
    private String rawMaterialName; // "Buah Pisang"

    // Constructor Lengkap (Dari Database)
    public MenuComponent(int id, int menuId, String componentName, boolean needsRawCheck, String rawMaterialName) {
        this.id = id;
        this.menuId = menuId;
        this.componentName = componentName;
        this.needsRawCheck = needsRawCheck;
        this.rawMaterialName = rawMaterialName;
    }

    // Constructor Simpel (Untuk Input Admin Sementara)
    public MenuComponent(String componentName, boolean needsRawCheck, String rawMaterialName) {
        this.componentName = componentName;
        this.needsRawCheck = needsRawCheck;
        this.rawMaterialName = rawMaterialName;
    }

    public String getComponentName() { return componentName; }
    public boolean isNeedsRawCheck() { return needsRawCheck; }
    public String getRawMaterialName() { return rawMaterialName; }
}