package com.example.qrcodefyp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodefyp.MyApplication
import com.example.qrcodefyp.R
import com.example.qrcodefyp.model.UserAuth
import com.example.qrcodefyp.preference.AuthPreference
import com.example.qrcodefyp.preference.LanguagePreference

class SplashScreen : AppCompatActivity() {
    private lateinit var userAuth: UserAuth
    //intent Activity
    private lateinit var intentActivity: Activity
    private var isArabic: Boolean=false

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
        isArabic=LanguagePreference(this).language
//        if(isArabic){
//            MyApplication.setLocale(this,"ar")
//        }
        //        Boolean isArabic=new LanguagePreference(this).getLanguage();
//        if(isArabic){
//            MyApplication.setLocale(getApplicationContext(),"ar");
//        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, intentActivity::class.java))
            finish()
        },1000)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        MyApplication.setLocale(this, "ar")
    }
}