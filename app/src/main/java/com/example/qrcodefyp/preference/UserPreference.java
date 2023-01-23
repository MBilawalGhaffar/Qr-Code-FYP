package com.example.qrcodefyp.preference;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.qrcodefyp.model.User;

public class UserPreference {
    private final SharedPreferences sharedPreferences;
    public UserPreference (Context context){
        sharedPreferences=context.getSharedPreferences("USER",Context.MODE_PRIVATE);
    }
    public User getUser() {
        String name=sharedPreferences.getString("name","Ali");
        String uid=sharedPreferences.getString("uuid","00000000");
        String email=sharedPreferences.getString("email","ali@gmail.com");
        return new User(uid,name,email);

    }
    public void addUser(User user) {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("name",user.getName());
        myEdit.putString("uuid",user.getUUID());
        myEdit.putString("email",user.getEmail());
        myEdit.apply();
    }
}
