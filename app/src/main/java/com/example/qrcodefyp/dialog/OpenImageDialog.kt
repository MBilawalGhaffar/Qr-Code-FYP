package com.example.qrcodefyp.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.qrcodefyp.R

class OpenImageDialog(context: Context,url:String,style:Int):Dialog(context,style) {
    private val mContext=context
    private val mUrl=url
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.open_image_layout)
        val receiptImage: ImageView =findViewById(R.id.receiptImage);
        Glide.with(mContext).load(mUrl).into(receiptImage)
    }
}