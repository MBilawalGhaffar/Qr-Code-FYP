package com.example.qrcodefyp.model;

public class BillModel {
    private String id;
    private String total_bill;
    private String description;
    private String bill_date;

    public BillModel(String id, String total_bill, String description, String bill_date) {
        this.id = id;
        this.total_bill = total_bill;
        this.description = description;
        this.bill_date = bill_date;
    }

    public BillModel() {
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

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }
}
