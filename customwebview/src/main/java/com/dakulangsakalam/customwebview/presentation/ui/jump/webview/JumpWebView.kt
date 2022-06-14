package com.dakulangsakalam.customwebview.presentation.ui.jump.webview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.AsyncTask
import android.os.Environment
import android.os.Message
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dakulangsakalam.customwebview.R
import com.dakulangsakalam.customwebview.databinding.CustomJumpWebViewBinding
import com.dakulangsakalam.customwebview.presentation.helper.PermissionHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class JumpWebView(context: Context, attrs: AttributeSet): WebView(context,attrs) {

    private val binding: CustomJumpWebViewBinding =
        CustomJumpWebViewBinding.inflate(LayoutInflater.from(context), this, true)

    private val permissionChecker by lazy {
        PermissionHelper(context)
    }

    private val downloadListener = DownloadListener { p0, _, _, _, _ ->
        val uri = Uri.parse(p0)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    var activityResultMultiplePermission = (context as AppCompatActivity).registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ActivityResultCallback<Map<String?, Boolean?>> { result: Map<String?, Boolean?> ->
            for ((_, value) in result) {
                if (!value!!) return@ActivityResultCallback
            }
            if (permissionChecker.hasAllPermissionsGranted()) saveImage.execute()
            else permissionChecker.showDialog()
        })

    private var imgurl: String? = null



    init {
        init()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {

        with(binding.wvContent){
            with(settings){
                javaScriptEnabled = true
                defaultTextEncodingName = "UTF-8"
                cacheMode = WebSettings.LOAD_NO_CACHE
                useWideViewPort = true
                pluginState = WebSettings.PluginState.ON
                domStorageEnabled = true
                builtInZoomControls = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                loadWithOverviewMode = true
                blockNetworkImage = true
                loadsImagesAutomatically = true
                setSupportZoom(false)
                setSupportMultipleWindows(true)
            }
            requestFocusFromTouch()
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            setDownloadListener(downloadListener)
        }

        val webseting: WebSettings = binding.wvContent.settings
        with(webseting){
            val appCacheDir = context.getDir("cache",
                AppCompatActivity.MODE_PRIVATE
            ).path
            domStorageEnabled = true
            setAppCacheMaxSize((1024 * 1024 * 8).toLong())
            setAppCachePath(appCacheDir)
            allowFileAccess = true
            setAppCacheEnabled(true)
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        binding.wvContent.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                binding.pbLoading.progress = newProgress
                if (newProgress == 100) {
                    binding.wvContent.settings.blockNetworkImage = false
                }
            }

            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val newWebView = WebView(context)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        binding.wvContent.loadUrl(url)
                        return true
                    }
                }
                return true
            }
        }

        val settings: WebSettings = binding.wvContent.settings
        settings.javaScriptEnabled = true
        binding.wvContent.setOnLongClickListener { v: View ->
            val result = (v as WebView).hitTestResult
            val type = result.type
            if (type == HitTestResult.UNKNOWN_TYPE) return@setOnLongClickListener false
            when (type) {
                HitTestResult.PHONE_TYPE -> {}
                HitTestResult.EMAIL_TYPE -> {}
                HitTestResult.GEO_TYPE -> {}
                HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {}
                HitTestResult.IMAGE_TYPE -> {
                    imgurl = result.extra
                    dialogList()
                }
                else -> {}
            }
            true
        }

        binding.wvContent.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.pbLoading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.pbLoading.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                val builder = AlertDialog.Builder(context)
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                message += " Do you want to continue anyway?"
                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)
                builder.setPositiveButton(
                    "Continue"
                ) { _: DialogInterface?, _: Int -> handler.proceed() }
                builder.setNegativeButton(
                    "Cancel"
                ) { _: DialogInterface?, _: Int -> handler.cancel() }
                val dialog = builder.create()
                dialog.show()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return super.shouldOverrideUrlLoading(view, url)
                } else if (url.startsWith("intent:")) {
                    val urlSplit = url.split("/").toTypedArray()
                    var send = ""
                    if (urlSplit[2] == "user") {
                        send = "https://m.me/" + urlSplit[3]
                    } else if (urlSplit[2] == "ti") {
                        val data = urlSplit[4]
                        val newSplit = data.split("#").toTypedArray()
                        send = "https://line.me/R/" + newSplit[0]
                    } // showToast(url);
                    val newInt = Intent(Intent.ACTION_VIEW, Uri.parse(send))
                    context.startActivity(newInt)
                } else {
                    try {
                        val `in` = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(`in`)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Jump failed", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
        }

        binding.wvContent.setOnKeyListener { _: View?, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK && binding.wvContent.canGoBack()) {
                    binding.wvContent.goBack()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }

    private fun dialogList() {
        val items = arrayOf("Save Picture", "Cancel")
        val builder = AlertDialog.Builder(context, 3)
        builder.setItems(items) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
            if ("Save Picture" == items[which]) {
                if (permissionChecker.isLackPermissions()) permissionChecker.requestPermissions(activityResultMultiplePermission)
                else saveImage.execute()
            }
        }
        builder.create().show()
    }

    @SuppressLint("StaticFieldLeak")
    private val saveImage =  object: AsyncTask<String?, Void?, String>() {
        override fun onPostExecute(result: String) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        }

        override fun doInBackground(vararg p0: String?): String {
            var result: String
            try {
                val sdcard = Environment.getExternalStorageDirectory().toString()
                var file = File("$sdcard/Download")
                if (!file.exists()) {
                    file.mkdirs()
                }
                val idx: Int = imgurl!!.lastIndexOf(".")
                val ext: String = imgurl!!.substring(idx)
                file = File(sdcard + "/Download/" + Date().time + ext)
                var inputStream: InputStream? = null
                val url = URL(imgurl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 20000
                if (conn.responseCode == 200) {
                    inputStream = conn.inputStream
                }
                val buffer = ByteArray(4096)
                var len: Int
                val outStream = FileOutputStream(file)
                while (true) {
                    assert(inputStream != null)
                    if (inputStream!!.read(buffer).also { len = it } == -1) break
                    outStream.write(buffer, 0, len)
                }
                outStream.close()
                result = "Saved successfully"
            } catch (e: java.lang.Exception) {
                result = "Failed to save！" + e.localizedMessage
            }
            return result
        }
    }

    override fun canGoBack(): Boolean {
       return binding.wvContent.canGoBack()
    }

    override fun goBack() {
        binding.wvContent.goBack()
    }

    override fun loadUrl(url: String) {
        binding.wvContent.loadUrl(url)
    }

    override fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>) {
        binding.wvContent.loadUrl(url, additionalHttpHeaders)
    }
}