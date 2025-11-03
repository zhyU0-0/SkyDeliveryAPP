package com.test.sky_delivery_app.request

import android.content.SharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/" // 去掉方法中的 baseUrl
    private lateinit var sharedPreferences: SharedPreferences

    // 初始化方法
    fun init(sharedPrefs: SharedPreferences) {
        sharedPreferences = sharedPrefs
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val token = sharedPreferences.getString("token","").toString()
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("token",token)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}