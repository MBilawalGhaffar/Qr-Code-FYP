package com.example.qrcodefyp;

import android.app.Application;

import com.example.qrcodefyp.model.User;
import com.example.qrcodefyp.preference.UserPreference;
import com.example.qrcodefyp.util.FirebaseUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        User user = new UserPreference(MyApplication.this).getUser();
        if(!user.getUUID().equals("00000000")){
            FirebaseUtil.USER = new UserPreference(MyApplication.this).getUser();
        }
    }
}
