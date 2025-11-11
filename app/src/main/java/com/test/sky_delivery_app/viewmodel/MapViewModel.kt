package com.test.sky_delivery_app.viewmodel

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.amap.api.maps.AMap
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import kotlin.collections.isNotEmpty

class MapViewModel(val context: Context,val sharedPreferences: SharedPreferences) : ViewModel(){

    var goat = mutableStateOf("桂林机场")
    var location = mutableStateOf(sharedPreferences.getString("location","广西"))
    var car = mutableStateOf(sharedPreferences.getInt("car",0))

    var aMap: AMap? = null
    // 权限请求码
    val PERMISSION_REQUEST_CODE = 1001
    val is_show_select = mutableStateOf(false)

    // 需要的权限列表
    val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun startNavigation(end:Poi) {
        try {
            /*// 创建起点PoiItem（天安门）
            val startPoint = LatLng(39.90923, 116.397428)
            val startPoi = createPoiItem(startPoint, "天安门")

            // 创建终点PoiItem（故宫）
            val endPoint = LatLng(39.916345, 116.397155)
            val endPoi = createPoiItem(endPoint, "故宫博物院")*/
            /*val start = Poi("龙城花园", null, "B000A8UF3J")*/
            /*val end = Poi("", null, null)*/
            //获取交通工具
            val car = sharedPreferences.getInt("car",0)
            var naviType = AmapNaviType.DRIVER
            when(car){
                0 ->{
                    naviType = AmapNaviType.DRIVER//汽车
                }
                1 ->{
                    naviType = AmapNaviType.MOTORCYCLE//摩托
                }
                2 ->{
                    naviType = AmapNaviType.RIDE//自行车
                }
                3 ->{
                    naviType = AmapNaviType.WALK//走路
                }
            }

            // 创建导航参数
            val naviParams = AmapNaviParams(
                null,  // 起点
                null,  // 途经点列表
                end,   // 终点
                naviType,  // 导航类型：驾车
                AmapPageType.ROUTE    // 页面类型：路径规划
            )

            // 启动导航页面
            AmapNaviPage.getInstance().showRouteActivity(
                context,
                naviParams,
                null  // 导航监听器
            )

        } catch (e: Exception) {
            Toast.makeText(context, "启动导航失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    fun search(){
        val location = sharedPreferences.getString("location","广西")
        // 1. 创建查询对象
        val query = PoiSearch.Query(goat.value, "", location)
        query.pageSize = 5 // 设置每页返回数量

        // 2. 初始化PoiSearch对象并设置监听
        val poiSearch = PoiSearch(context, query)
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiSearched(poiResult: PoiResult?, errorCode: Int) {
                if (errorCode == 1000 && poiResult != null) { // 1000表示成功
                    val pois = poiResult.pois
                    if (pois != null && pois.isNotEmpty()) {
                        // 3. 获取第一个结果的POI ID
                        val firstPoi = pois[0]
                        val poiId = firstPoi.poiId
                        val poiName = firstPoi.title
                        Log.d("POI_SEARCH", "找到POI: $poiName, ID: $poiId")
                        // 接下来可以使用这个poiId进行导航等操作
                        val end = Poi(poiName, null, poiId)
                        startNavigation(end)
                        return
                    }
                }
            }

            override fun onPoiItemSearched(
                p0: com.amap.api.services.core.PoiItem,
                errorCode: Int
            ) {

            }
        })
        poiSearch.searchPOIAsyn()
    }

    fun updateLocation(){
        sharedPreferences.edit {
            putString("location",location.value).apply()
        }
        Toast.makeText(context,"更改成功", Toast.LENGTH_SHORT).show()
    }
    fun updateCar(){
        sharedPreferences.edit {
            putInt("car",car.value).apply()
        }
        Toast.makeText(context,"更改成功", Toast.LENGTH_SHORT).show()
    }
    fun showSelectCar(){
        is_show_select.value = true
    }


}