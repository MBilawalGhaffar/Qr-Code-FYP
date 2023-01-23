package com.example.qrcodefyp.preference;



import android.content.Context;
import android.content.SharedPreferences;

import com.example.qrcodefyp.model.UserAuth;

public class AuthPreference {
    private final SharedPreferences sharedPreferences;

    public AuthPreference(Context context) {
        sharedPreferences=context.getSharedPreferences("AUTH",Context.MODE_PRIVATE);
    }

    public UserAuth getAuth() {
        boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
        boolean isRemember=sharedPreferences.getBoolean("isRemember",false);
        return new UserAuth(isLogin,isRemember);

    }
    public void addAuth(UserAuth userAuth) {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putBoolean("isLogin",userAuth.isLogin());
        myEdit.putBoolean("isRemember",userAuth.isRemember());
        myEdit.apply();
    }
}
