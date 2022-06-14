package com.dakulangsakalam.customwebview.presentation.helper

import com.dakulangsakalam.customwebview.data.repository.JumpRepo
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {
    fun retrofitService(URL: String?): JumpRepo {
        return Retrofit.Builder()
            .baseUrl(URL ?: "")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHeaders())
            .build()
            .create(JumpRepo::class.java)
    }

    private fun getHeaders(): OkHttpClient{
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val request: Request =
                chain.request()
                    .newBuilder()
                    .addHeader("Accept", "*/*")
                    .addHeader("Connection", "Keep-Alive")
                    .build()
            chain.proceed(request)
        }
        return httpClient.build()
    }
}