package com.test.sky_delivery_app.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.sky_delivery_app.R
import com.test.sky_delivery_app.pojo.response.WeatherResponse
import com.test.sky_delivery_app.request.Repository
import com.test.sky_delivery_app.request.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class WeatherViewModel(val context: Context,val sharedPreferences: SharedPreferences): ViewModel() {

    var weather = mutableStateOf(WeatherResponse())

    private val authRepository = Repository(
        context,
        RetrofitClient.authApiService,
        RetrofitClient.weatherService,
        sharedPreferences
    )

    suspend fun getWeather(){
        val data = authRepository.getWeather()
        if(data.status == 1){
            weather.value = data
        }else{
            Toast.makeText(context, "获取天气失败", Toast.LENGTH_SHORT).show()
        }
    }
    private val CHANNEL_ID = "weather_alert_channel"
    private var notificationId = 1

    init {
        createNotificationChannel()
    }

    /**
     * 分析天气条件并在温度大于30度或下雨时发送通知提醒
     * @param temperature 当前温度
     * @param isRaining 是否正在下雨
     */
    fun analyzeWeatherAndNotify() {
        viewModelScope.launch {
            getWeather()
            Log.v("Weather", weather.value.toString())
            val maxTemperature = context.getString(R.string.maxT)
            if(weather.value.status == 1){
                val temperature = weather.value.lives[0].temperature.toFloat()
                val isRaining = weather.value.lives[0].weather == "雨"
                if (temperature > maxTemperature.toInt() || isRaining) {
                    sendWeatherNotification(temperature, isRaining)
                }
            }
        }
    }

    /**
     * 创建通知渠道（Android 8.0及以上版本需要）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "天气提醒"
            val descriptionText = "天气异常提醒通知"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 发送天气通知
     */
    private fun sendWeatherNotification(temperature: Float, isRaining: Boolean) {
        Log.v("Weather", "sendWeatherNotification")
        val message = if (!isRaining) {
            "高温警告：当前温度 ${temperature}°C，请注意防暑降温！"
        } else {
            "天气提醒：当前正在下雨，请注意出行安全！"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // 使用系统默认图标
            .setContentTitle("天气提醒")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId++, builder.build())
    }

}