package com.sentragizi.shared.models;

public class ProductionKitchen {
    private int id;
    private String name;
    private String address;
    private boolean isActive;

    public ProductionKitchen() {}

    public ProductionKitchen(int id, String name, String address, boolean isActive) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.isActive = isActive;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return name + " - " + address;
    }
}