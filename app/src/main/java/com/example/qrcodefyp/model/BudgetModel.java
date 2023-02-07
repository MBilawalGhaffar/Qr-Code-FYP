package com.example.qrcodefyp.model;

public class BudgetModel {
    private int totalBudget;
    private int usedBudget;
    private int remainingBudget;
    private String currency;

    public BudgetModel(int totalBudget, int usedBudget, int remainingBudget, String currency) {
        this.totalBudget = totalBudget;
        this.usedBudget = usedBudget;
        this.remainingBudget = remainingBudget;
        this.currency = currency;
    }

    public BudgetModel() {
    }

    public int getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(int totalBudget) {
        this.totalBudget = totalBudget;
    }

    public int getUsedBudget() {
        return usedBudget;
    }

    public void setUsedBudget(int usedBudget) {
        this.usedBudget = usedBudget;
    }

    public int getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(int remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
