package com.example.qrcodefyp.callback;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;

public interface OnImagePicked {
    public void onImagePicked(@Nullable Uri uri,@Nullable Bitmap bitmap);
}
