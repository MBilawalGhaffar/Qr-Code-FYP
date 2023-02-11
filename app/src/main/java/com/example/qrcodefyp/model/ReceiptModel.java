package com.example.qrcodefyp.model;

public class ReceiptModel {
    private String id;
    private String total_bill;
    private String description;
    private String category;
    private String payment;
    private String expiry_date;
    private String image_url;
    private String currency;

    public ReceiptModel(String id, String total_bill, String description, String category, String payment, String expiry_date, String image_url, String currency) {
        this.id=id;
        this.total_bill = total_bill;
        this.description = description;
        this.category = category;
        this.payment = payment;
        this.expiry_date = expiry_date;
        this.image_url = image_url;
        this.currency=currency;
    }

    public ReceiptModel() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotal_bill() {
        return total_bill;
    }

    public void setTotal_bill(String total_bill) {
        this.total_bill = total_bill;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
