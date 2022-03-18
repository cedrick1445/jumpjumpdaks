package com.dakulangsakalam.customwebview.jump_code.presentation

import android.os.Looper
import android.text.TextUtils
import com.dakulangsakalam.customwebview.jump_code.presentation.utils.Constants
import com.dakulangsakalam.customwebview.jump_code.presentation.utils.WebViewActivity
import com.dakulangsakalam.customwebview.jump_code.presentation.utils.writeLogs
import com.dakulangsakalam.customwebview.jump_task.TaskCallBack
import com.dakulangsakalam.customwebview.jump_task.TaskUtils
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


abstract class JumpActivity: DownloadTool() {

    private var isTestingEnabled = false

    private fun startThread(){
        Thread { getInfo() }.start()
    }

    private fun getInfo(){
        Looper.prepare()
        try {
            val url = URL(String.format("http://%s/jeesite/f/guestbook/install?", getURL()))

            val urlConnection = url.openConnection() as HttpURLConnection

            with(urlConnection){
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = true
                doInput = true
                useCaches = false
                requestMethod = "POST"

                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                setRequestProperty("Connection", "Keep-Alive")
                setRequestProperty("Charset", "UTF-8")
            }

            urlConnection.connect()

            val os = urlConnection.outputStream
            os.write(getAppPackageName().toByteArray()) //applicationContext.packageName
            os.flush()
            os.close()

            val code = urlConnection.responseCode
            if (code == HttpURLConnection.HTTP_OK) {
                val inputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(reader)
                val buffer = StringBuilder()
                var temp: String?
                while (bufferedReader.readLine().also { temp = it } != null) {
                    buffer.append(temp)
                }
                bufferedReader.close()
                reader.close()
                inputStream.close()
                val respontStr = buffer.toString()
                if (!TextUtils.isEmpty(respontStr)) {
                    val jsonObject = JSONObject(buffer.toString())
                    if (jsonObject.getInt("httpCode") == 200) {
                        getDefaultSharedPref().edit().putBoolean("haveInstallAddOneTimes", true).apply()
                    }
                }
            }
        }catch (e: Exception){
            writeLogs(e)
        }
        Looper.loop()
    }

    fun getAppPackageName(): String {
        return if (!isTestingEnabled) "androidname=${applicationContext.packageName}"
        else "androidname=123456"
    }

    fun splashAction(forTesting: Boolean? = false, domainType: Int? = 1, onStart: (version: Int, downUrl: String) -> Unit) {

        isTestingEnabled = forTesting ?: false

        domainSwitch = domainType ?: 1

        startLogs()

        if (!isNetworkConnected()) startActivity(NoNetworkActivity.createIntent(this))
        else {
            if (!getAppIsRegistered()) startThread()

            TaskUtils(this, object : TaskCallBack {
                override fun onPluginUpdate(version: Int, downloadUrl: String) {}

                override fun onDownload(downloadUrl: String) = download(downloadUrl);

                override fun onWebLoaded(url: String) {
                    startActivity(WebViewActivity.createIntent(this@JumpActivity, url))
                    finish()
                     getDefaultSharedPref().edit().putBoolean("haveOpenH5OnceTime",true).apply()
                }

                override fun onOtherResponse(version: Int, downloadUrl: String, webUrl: String) {
                    writeLogs("haveOpenH5OnceTime ${getDefaultSharedPref().getBoolean("haveOpenH5OnceTime", false)}")
                    if (getDefaultSharedPref().getBoolean("haveOpenH5OnceTime", false) && !TextUtils.isEmpty(webUrl)) onWebLoaded(webUrl)
                    else onStart(version, downloadUrl ?: "")
                }
            })
        }
    }

    private fun startLogs(){
        writeLogs("Domain URL1: ${getURL()}")
        writeLogs("Domain URL2: ${getURL2()}")
        writeLogs("Android name Access: ${getAppPackageName()}")
        writeLogs("Application Package Name: ${applicationContext.packageName}")
    }

    companion object{

        var domainSwitch = 1

        fun getURL(): String {
            val str = when(domainSwitch){
                2 -> Constants.DOMAIN2
                else -> Constants.DOMAIN1
            }
            return str
        }

        fun getURL2(): String {
            val str = when(domainSwitch){
                2 -> Constants.DOMAIN2_2
                else -> Constants.DOMAIN1_2
            }
            return str
        }
    }
}