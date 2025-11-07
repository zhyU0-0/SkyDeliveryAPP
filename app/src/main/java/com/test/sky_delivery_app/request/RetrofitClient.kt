package com.test.sky_delivery_app.request

import android.content.SharedPreferences
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(sharedPrefs: SharedPreferences) {
        sharedPreferences = sharedPrefs
    }

    private fun getBaseUrl(): String {
        return "http://" + sharedPreferences.getString("ip", "10.0.2.2:8088") + "/"
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                // 每次请求时动态获取最新的 token
                val token = sharedPreferences.getString("token", "") ?: ""
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")

                // 只有 token 不为空时才添加
                if (token.isNotEmpty()) {
                    requestBuilder.addHeader("token", token)
                }

                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // 提供重新创建实例的方法（当 IP 或重要配置改变时）
    fun reset() {
        // 由于使用了 lazy，重新访问时会重新创建
        // 或者可以创建新的 Retrofit 实例
    }
}