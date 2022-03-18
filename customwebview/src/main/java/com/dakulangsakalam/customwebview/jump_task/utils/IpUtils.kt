package com.dakulangsakalam.customwebview.jump_task.utils

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


fun getNetArea(): String {
    var country = ""
    var region = ""
    var city = ""
    try {
        val address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip"
        val url = URL(address)
        val connection = url.openConnection() as HttpURLConnection
        with(connection){
            connectTimeout = 5000
            readTimeout = 5000
            useCaches = false
            requestMethod = "GET"
            setRequestProperty(
                "user-agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.7 Safari/537.36"
            )
        }

        if (connection.responseCode == 200) {
            val `in` = connection.inputStream
            val reader = BufferedReader(InputStreamReader(`in`))
            var tmpString = ""
            val retJSON = StringBuilder()
            while (reader.readLine().also { tmpString = it } != null) {
                retJSON.append(
                    """
                        $tmpString
                        
                        """.trimIndent()
                )
            }
            val jsonObject = JSONObject(retJSON.toString())
            val code = jsonObject.getString("code")
            if (code == "0") {
                val data = jsonObject.getJSONObject("data")
                country = data.getString("country")
                region = data.getString("region")
                city = data.getString("city")
            }
        }
    } catch (var13: Exception) {
    }
    return String.format("country=%s&region=%s&city=%s", country, region, city)
}