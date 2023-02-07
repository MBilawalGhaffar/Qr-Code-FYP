package com.example.qrcodefyp.preference;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.qrcodefyp.model.BudgetModel;
import com.example.qrcodefyp.model.User;

public class BudgetPreference {
    private final SharedPreferences sharedPreferences;
    public BudgetPreference(Context context){
        sharedPreferences=context.getSharedPreferences("BUDGET",Context.MODE_PRIVATE);
    }
    public BudgetModel getBudget() {
        int totalBudget=sharedPreferences.getInt("totalBudget",100);
        int usedBudget=sharedPreferences.getInt("usedBudget",20);
        int remainingBudget=sharedPreferences.getInt("remainingBudget",80);
        String currency=sharedPreferences.getString("currency","SAR");
        return new BudgetModel(totalBudget,usedBudget,remainingBudget,currency);

    }
    public void addBudget(BudgetModel budgetModel) {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("totalBudget",budgetModel.getTotalBudget());
        myEdit.putInt("usedBudget",budgetModel.getUsedBudget());
        myEdit.putInt("remainingBudget",budgetModel.getRemainingBudget());
        myEdit.putString("currency",budgetModel.getCurrency());
        myEdit.apply();
    }
}
