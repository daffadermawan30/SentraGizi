package com.sentragizi.shared.models;

public class InspectionDetail {
    private int id;
    private long inspectionId;
    private int componentId;
    private int vendorId;
    private String photoPath;
    private boolean bauOk;
    private boolean rasaOk;
    private boolean teksturOk;
    private String statusAi;
    private String statusFinal;

    // Constructor Kosong
    public InspectionDetail() {}

    // Constructor Lengkap
    public InspectionDetail(int componentId, int vendorId, boolean bau, boolean rasa, boolean tekstur, String ai, String finalStatus, String path) {
        this.componentId = componentId;
        this.vendorId = vendorId;
        this.bauOk = bau;
        this.rasaOk = rasa;
        this.teksturOk = tekstur;
        this.statusAi = ai;
        this.statusFinal = finalStatus;
        this.photoPath = path;
    }

    // Getter dan Setter
    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public int getComponentId() { return componentId; }
    
    public boolean isBauOk() { return bauOk; }
    public boolean isRasaOk() { return rasaOk; }
    public boolean isTeksturOk() { return teksturOk; }
    
    public String getStatusFinal() { return statusFinal; }
    public String getPhotoPath() { return photoPath; }

    // --- BAGIAN YANG SUDAH DIPERBAIKI ---
    public String getStatusAi() {
        return statusAi; // Sekarang mengembalikan nilai, bukan Error
    }
}