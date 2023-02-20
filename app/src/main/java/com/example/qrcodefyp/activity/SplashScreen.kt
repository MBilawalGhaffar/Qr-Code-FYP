package com.example.qrcodefyp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.qrcodefyp.MyApplication
import com.example.qrcodefyp.R
import com.example.qrcodefyp.model.UserAuth
import com.example.qrcodefyp.preference.AuthPreference

class SplashScreen : AppCompatActivity() {
    private lateinit var userAuth: UserAuth
    //intent Activity
    private lateinit var intentActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        userAuth=AuthPreference(this).auth
        if(userAuth.isLogin){
            if(userAuth.isRemember){
                intentActivity=Home()
            }else{
                intentActivity=LoginActivity()
            }
        }else{
            intentActivity=LoginActivity()
        }
        MyApplication.setLocale(this,"ar")
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, intentActivity::class.java))
            finish()
        },1000)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}