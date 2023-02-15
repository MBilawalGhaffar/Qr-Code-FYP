package com.example.qrcodefyp.model;

public class ShopModel {
    private int id;
    private String name;
    private String discount;

    public ShopModel(int id, String name, String discount) {
        this.id=id;
        this.name = name;
        this.discount = discount;
    }

    public ShopModel() {
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

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
