package com.example.qrcodefyp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.example.qrcodefyp.model.User;
import com.example.qrcodefyp.preference.LanguagePreference;
import com.example.qrcodefyp.preference.UserPreference;
import com.example.qrcodefyp.util.FirebaseUtil;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        User user = new UserPreference(MyApplication.this).getUser();
        if(!user.getUUID().equals("00000000")){
            FirebaseUtil.USER = new UserPreference(MyApplication.this).getUser();
        }
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        Boolean isArabic=new LanguagePreference(this).getLanguage();
//        if(isArabic){
//            MyApplication.setLocale(getApplicationContext(),"ar");
//        }

    }
    public static void setLocale(Activity activity, String languageCode) {
        Boolean isArabic=new LanguagePreference(activity).getLanguage();
        Log.d("LanguagePreference","***********************************");
        Log.d("LanguagePreference","***********************************");
        Log.d("LanguagePreference","***********************************");
        Log.d("LanguagePreference","isArabic"+isArabic);
        if(!isArabic){
            Log.d("LanguagePreference","en set");
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else {
            Log.d("LanguagePreference","ar set");
            Locale locale = new Locale("ar","SA");
            Locale.setDefault(locale);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

    }
}
