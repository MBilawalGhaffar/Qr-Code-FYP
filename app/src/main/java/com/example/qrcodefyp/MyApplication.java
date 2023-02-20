package com.example.qrcodefyp;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.example.qrcodefyp.model.User;
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

    }
    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode,"SA");
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
