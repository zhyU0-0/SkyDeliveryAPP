package com.test.sky_delivery_app.request

import com.test.sky_delivery_app.pojo.response.PoiResponse
import com.test.sky_delivery_app.pojo.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("v3/weather/weatherInfo")
    suspend fun getWeather(
        @Query("key")key:String,
        @Query("city")city:String,
        @Query("extensions")extensions:String,
        @Query("output")output:String
    ): WeatherResponse

    @GET("v5/place/text")
    suspend fun getPoi(
        @Query("key")key:String,
        @Query("keywords")keywords:String
    ): PoiResponse


}