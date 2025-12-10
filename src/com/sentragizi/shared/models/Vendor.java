package com.sentragizi.shared.models;

public class Vendor {
    private int id;
    private String name;
    private String specialty; // "Buah", "Daging"

    public Vendor(int id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }

    @Override
    public String toString() { // Biar di ComboBox muncul namanya
        return name + " (" + specialty + ")";
    }
}