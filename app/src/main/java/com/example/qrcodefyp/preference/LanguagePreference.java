package com.example.qrcodefyp.preference;



import android.content.Context;
import android.content.SharedPreferences;

import com.example.qrcodefyp.model.UserAuth;

public class LanguagePreference {
    private final SharedPreferences sharedPreferences;

    public LanguagePreference(Context context) {
        sharedPreferences=context.getSharedPreferences("Language",Context.MODE_PRIVATE);
    }

    public boolean getLanguage() {
        boolean isArabic=sharedPreferences.getBoolean("isArabic",false);
        return isArabic;

    }
    public void addLanguage(boolean isArabic) {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("isArabic",isArabic);
        myEdit.apply();
    }
}
