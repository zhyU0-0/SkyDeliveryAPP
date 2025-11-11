package com.test.sky_delivery_app.pojo.response

import java.io.Serializable

data class WeatherResponse(
    // 状态（1：成功；0：失败）
    val status: Int = 0,
    // 返回结果总数目
    val count: Int = 0,
    // 返回的状态信息
    val info: String = "",
    // 返回状态说明（10000代表正确）
    val infocode: String = "",
    // 实况天气数据信息
    val lives: List<LiveWeather> = emptyList(),
    // 预报天气信息数据
    val forecast: List<ForecastWeather> = emptyList()
)

/**
 * 实况天气数据模型
 */
data class LiveWeather(
    // 省份名
    val province: String = "",
    // 城市名
    val city: String = "",
    // 区域编码
    val adcode: String = "",
    // 天气现象（汉字描述）
    val weather: String = "",
    // 实时气温（单位：摄氏度）
    val temperature: String = "",
    // 风向描述
    val winddirection: String = "",
    // 风力级别（单位：级）
    val windpower: String = "",
    // 空气湿度
    val humidity: String = "",
    // 数据发布的时间
    val reporttime: String = ""
)

/**
 * 预报天气数据模型
 */
data class ForecastWeather(
    // 城市名称
    val city: String = "",
    // 城市编码
    val adcode: String = "",
    // 省份名称
    val province: String = "",
    // 预报发布时间
    val reporttime: String = "",
    // 预报数据列表（按顺序为当天、第二天、第三天）
    val casts: List<ForecastCast> = emptyList()
) : Serializable

/**
 * 单天预报数据模型
 */
data class ForecastCast(
    // 日期（格式示例：2025-11-11）
    val date: String = "",
    // 星期几（汉字描述，示例：星期二）
    val week: String = "",
    // 白天天气现象
    val dayweather: String = "",
    // 晚上天气现象
    val nightweather: String = "",
    // 白天温度（单位：摄氏度）
    val daytemp: String = "",
    // 晚上温度（单位：摄氏度）
    val nighttemp: String = "",
    // 白天风向
    val daywind: String = "",
    // 晚上风向
    val nightwind: String = "",
    // 白天风力（单位：级）
    val daypower: String = "",
    // 晚上风力（单位：级）
    val nightpower: String = ""
)
