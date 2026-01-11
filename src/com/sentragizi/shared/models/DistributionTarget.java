package com.sentragizi.shared.models;

public class DistributionTarget {
    private int id;
    private String name;
    private String location;
    private String type; 
    private boolean isActive;

    public DistributionTarget() {}

    public DistributionTarget(int id, String name, String location, String type, boolean isActive) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.type = type;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + name + " - " + location;
    }
}