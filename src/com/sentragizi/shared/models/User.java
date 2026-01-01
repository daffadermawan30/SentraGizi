package com.sentragizi.shared.models;

public class User {
    private int id;
    private String username;
    private String role;     // "ADMIN" atau "INSPECTOR"
    private String fullname;

    public User(int id, String username, String role, String fullname) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.fullname = fullname;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getFullname() { return fullname; }
}