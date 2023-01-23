package com.example.qrcodefyp.model;

public class User {
    private String UUID;
    private String Name;
    private String Email;

    public User(String UUID, String name, String email) {
        this.UUID = UUID;
        Name = name;
        Email = email;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
