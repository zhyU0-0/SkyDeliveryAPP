package com.test.sky_delivery_app.pojo.response

import java.io.Serializable

/**
 * POI数据响应模型（桂林相关地点信息）
 */
data class PoiResponse(
    // 结果总数目
    val count: Int = 0,
    // 返回状态说明（10000代表正确）
    val infocode: String = "",
    // POI地点列表（键为索引字符串，值为具体地点信息）
    val pois: List<PoiItem> = listOf<PoiItem>(),
    // 返回的状态信息
    val info: String = ""
)

/**
 * POI单项地点数据模型
 */
data class PoiItem(
    // 父POI ID（无父节点时为空字符串）
    val parent: String = "",
    // 详细地址
    val address: String = "",
    // 距离信息（无数据时为空字符串）
    val distance: String = "",
    // 省份编码
    val pcode: Int = 0,
    // 行政区划编码
    val adcode: Int = 0,
    // 省份名称
    val pname: String = "",
    // 城市名称
    val cityname: String = "",
    // 地点类型描述（格式：一级分类;二级分类;三级分类）
    val type: String = "",
    // 地点类型编码
    val typecode: String = "",
    // 区县名称
    val adname: String = "",
    // 城市电话区号
    val citycode: String = "",
    // 地点名称
    val name: String = "",
    // 经纬度字符串（格式：经度,纬度）
    val location: String = "",
    // POI唯一ID
    val id: String = ""
)