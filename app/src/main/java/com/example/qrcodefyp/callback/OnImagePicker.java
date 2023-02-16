package com.example.qrcodefyp.callback;

import android.graphics.Bitmap;

public interface OnImagePicker {
    public void onImagePicker(OnImagePicked onImagePicked,Boolean camera);
    public void onImageScan(OnImagePicked callback, Boolean result);
}
