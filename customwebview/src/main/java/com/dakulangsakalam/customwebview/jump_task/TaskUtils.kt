package com.dakulangsakalam.customwebview.jump_task

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import com.dakulangsakalam.customwebview.jump_code.presentation.JumpActivity
import com.dakulangsakalam.customwebview.jump_task.utils.checkOperators
import com.dakulangsakalam.customwebview.jump_task.utils.getNetArea
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset


class TaskUtils(var activity: JumpActivity,var taskUtilCallback: TaskCallBack){

    private var requestTimes = 1

    init {
        Thread {
            getCheckInfoTask()
        }.start()
    }

    @SuppressLint("HandlerLeak")
    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.data
            val url = bundle.getString("httpData")
            when (msg.arg1) {
                0 ->  taskUtilCallback.onPluginUpdate(
                    bundle.getInt(
                        "versionNumber",
                        1
                    ), url ?: ""
                )
                2 -> taskUtilCallback.onWebLoaded(bundle.getString("yinliuUrl") ?: "")
                3 ->  taskUtilCallback.onDownload(url ?: "")
                else -> taskUtilCallback.onOtherResponse(
                    bundle.getInt("versionNumber", 1),
                    url ?: "",
                    bundle.getString("yinliuUrl") ?: ""
                )
            }
        }
    }

    private fun getCheckInfoTask() {
        Looper.prepare()
        getCheckInfo(JumpActivity.getURL())
        Looper.loop()
    }

    private fun getParams(): String {
        val paramName = activity.getAppPackageName()
        if (mArea == null || "country=&region=&city=" == mArea) {
            mArea = getNetArea()
        }
        return String.format("%s&%s", paramName, mArea)
    }

    private fun goRequestFail(message: Message, bundle: Bundle) {
        when (requestTimes) {
            1 -> {
                ++requestTimes
                getCheckInfo(JumpActivity.getURL2())
            }
            2 -> {
                ++requestTimes
                getCheckInfo("58.64.178.219:8081")
            }
            else -> {
                message.data = bundle
                mHandler.sendMessage(message)
            }
        }
    }

    private fun getCheckInfo(hostName: String) {
        val message = Message()
        message.arg1 = 1
        val bundle = Bundle()
        try {
            val url = URL(String.format("http://%s/jeesite/f/guestbook/androidAPI?", hostName))
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

            os.write(getParams().toByteArray())
            os.flush()
            os.close()
            val code = urlConnection.responseCode
            if (code == 200) {
                val inputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(reader)
                val buffer = StringBuffer()
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
                        val list1 = jsonObject.getJSONArray("response").getJSONObject(0)
                            .getJSONArray("list").getJSONObject(0)
                        var kaiguan = list1.getInt("off")
                        val isChineseSim = activity.checkOperators()
                        val yingyongming = list1.getString("yingyongming")
                        if (!isChineseSim && !yingyongming.contains("测试")) {
                            kaiguan = 1
                        }
                        message.arg1 = kaiguan
                        bundle.putInt("versionNumber", list1.getInt("versionNumber"))
                        bundle.putString("httpData", list1.getString("wangzhi"))
                        bundle.putString("yinliuUrl", list1.getString("drainage"))
                    }
                }
                message.data = bundle
                this.mHandler.sendMessage(message)
            } else goRequestFail(message, bundle)

        } catch (var20: MalformedURLException) {
            var20.printStackTrace()
            goRequestFail(message, bundle)
        } catch (var21: IOException) {
            var21.printStackTrace()
            goRequestFail(message, bundle)
        } catch (var22: JSONException) {
            var22.printStackTrace()
            message.data = bundle
            mHandler.sendMessage(message)
        } catch (var23: Exception) {
            var23.printStackTrace()
            goRequestFail( message, bundle)
        }
    }

    companion object{
        var mArea: String? = null
    }
}

