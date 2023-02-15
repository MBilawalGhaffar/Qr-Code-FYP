package com.example.qrcodefyp.activity

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.DownloadListener
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.example.qrcodefyp.R
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import java.net.URL


class QrCodeScannerActivity : AppCompatActivity() {
    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val PERMISSION_REQUEST_CODE = 101
        private const val ZXING_SCAN_INTENT_ACTION = "com.google.zxing.client.android.SCAN"
        private const val CONTINUOUS_SCANNING_PREVIEW_DELAY = 500L
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_CAMERA_PERMISSION = 11
        private const val REQUEST_CODE_READ_PERMISSION = 12
        private const val IMAGE_CODE = 14
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).toTypedArray()
    }

    private lateinit var webView: WebView
    private lateinit var codeScanner: CodeScanner



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)
        webView= findViewById(R.id.webView)
        webView.getSettings().javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        requestAllPermission()
//        initScanner()

    }
    private fun requestAllPermission() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }
    private fun initScanner() {
        codeScanner = CodeScanner(this, findViewById(R.id.scanner_view))


//            .apply {
//            val isBackCamera=true
//            camera = if (isBackCamera) {
//                CodeScanner.CAMERA_BACK
//            } else {
//                CodeScanner.CAMERA_FRONT
//            }
//            val simpleAutoFocus=true
//            autoFocusMode = if (simpleAutoFocus) {
//                AutoFocusMode.SAFE
//            } else {
//                AutoFocusMode.CONTINUOUS
//            }
//            formats = SupportedBarcodeFormats.FORMATS
//            scanMode = ScanMode.SINGLE
//            isAutoFocusEnabled = true
//            isFlashEnabled = false
//            isTouchFocusEnabled = false
//            decodeCallback = DecodeCallback(::handleScannedBarcode)
//            errorCallback = ErrorCallback(::handleErrorBarcode)
//        }

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback { it ->
            runOnUiThread {
                Log.e("QRCODE","it.text ******"+it.text)
                Log.e("QRCODE","it.barcodeFormat ******"+it.barcodeFormat)
                Log.e("QRCODE","it.resultMetadata ******"+it.resultMetadata)
                Log.e("QRCODE","it.numBits ******"+it.numBits)
//                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                val intent = Intent()
                    .putExtra("SCAN_RESULT", it.text)
                    .putExtra("SCAN_RESULT_FORMAT", it.barcodeFormat.toString())
                val path = intent.getStringExtra("SCAN_RESULT_IMAGE_PATH")
                val url = URL(it.text)
                webView.loadUrl(it.text)
                Handler(Looper.myLooper()!!).postDelayed({
                    val  webViewHitTestResult = webView.hitTestResult
                    Log.e("QRCODE","webViewHitTestResult")
                    Log.e("QRCODE","getType   "+webViewHitTestResult.type)
                    if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE){
                        Toast.makeText(this,"Image true",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"Image false",Toast.LENGTH_SHORT).show()
                    }
                },2000)
                webView.visibility= View.VISIBLE

//                webView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
//                    val request = DownloadManager.Request(Uri.parse(url))
//                    request.allowScanningByMediaScanner()
//                    request.setNotificationVisibility(
//                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
//                    )
//                    request.setDestinationInExternalPublicDir(
//                        Environment.DIRECTORY_DOWNLOADS,  //Download folder
//                        "download"
//                    ) //Name of file
//                    val dm = getSystemService(
//                        DOWNLOAD_SERVICE
//                    ) as DownloadManager
//                    dm.enqueue(request)
//                })
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
        try {
            codeScanner.startPreview()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    private fun handleScannedBarcode(result: Result) {
        if (this.intent?.action == ZXING_SCAN_INTENT_ACTION) {
//            vibrateIfNeeded()
            finishWithResult(result)
            return
        }

    }
    private fun handleErrorBarcode(exception: Exception) {
        Toast.makeText(this,exception.message.toString(),Toast.LENGTH_SHORT).show()

    }
    private fun finishWithResult(result: Result) {
        val intent = Intent()
            .putExtra("SCAN_RESULT", result.text)
            .putExtra("SCAN_RESULT_FORMAT", result.barcodeFormat.toString())

        if (result.rawBytes?.isNotEmpty() == true) {
            intent.putExtra("SCAN_RESULT_BYTES", result.rawBytes)
        }

        result.resultMetadata?.let { metadata ->
            metadata[ResultMetadataType.UPC_EAN_EXTENSION]?.let {
                intent.putExtra("SCAN_RESULT_ORIENTATION", it.toString())
            }

            metadata[ResultMetadataType.ERROR_CORRECTION_LEVEL]?.let {
                intent.putExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL", it.toString())
            }

            metadata[ResultMetadataType.UPC_EAN_EXTENSION]?.let {
                intent.putExtra("SCAN_RESULT_UPC_EAN_EXTENSION", it.toString())
            }

            metadata[ResultMetadataType.BYTE_SEGMENTS]?.let {
                var i = 0
                @Suppress("UNCHECKED_CAST")
                for (seg in it as Iterable<ByteArray>) {
                    intent.putExtra("SCAN_RESULT_BYTE_SEGMENTS_$i", seg)
                    ++i
                }
            }
        }
        Toast.makeText(this,"Result",Toast.LENGTH_SHORT).show()

        this.apply {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    private fun vibrateIfNeeded() {
        val vibrate=true
        if (vibrate) {
            this.apply {
                runOnUiThread {

                }
            }
        }
    }
    private fun cameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(baseContext, REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        try {
            codeScanner.releaseResources()
        }catch (e:Exception){

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_PERMISSIONS -> {
                if (cameraPermissionGranted()) {
                    initScanner()
                } else {
                    Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}