package com.test.sky_delivery_app.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.AMap
import com.amap.api.maps.MapView
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.amap.api.navi.model.search.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.test.sky_delivery_app.R


class NavigaterActivity : AppCompatActivity() {
    private var aMap: AMap? = null
    private var Goat: String = "桂林理工大学"
    // 权限请求码
    private val PERMISSION_REQUEST_CODE = 1001

    // 需要的权限列表
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveIntentParams()
        initAMapPrivacy()

        checkPermissions()
        search(Goat)
    }
    private fun receiveIntentParams() {
        val intent = intent
        // 取出参数（第二个参数是默认值，防止未传参时为空）
        Goat = intent.getStringExtra("Goat") ?: "桂林电子科技大学"

        // 日志打印，验证参数是否接收成功
        Log.d("NavigaterActivity", "接收参数：Goat = ${Goat}")
    }
    private fun initAMapPrivacy() {
        try {
            // 必须在任何SDK调用之前设置
            AMapLocationClient.updatePrivacyShow(this, true, true)
            AMapLocationClient.updatePrivacyAgree(this, true)

/*            // 对于导航SDK，也需要设置
            com.amap.api.navi.AmapNavi.updatePrivacyShow(this, true, true)
            com.amap.api.navi.AmapNavi.updatePrivacyAgree(this, true)*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun checkPermissions() {
        val missingPermissions = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            initMap()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initMap()
            } else {
                Toast.makeText(this, "需要位置权限才能使用导航功能", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initMap() {
        aMap?.apply {
            // 启用定位图层
            uiSettings.isMyLocationButtonEnabled = true
            isMyLocationEnabled = true
        }
    }

    /**
     * 启动导航功能
     */
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
            // 创建导航参数
            val naviParams = AmapNaviParams(
                null,  // 起点
                null,  // 途经点列表
                end,   // 终点
                AmapNaviType.DRIVER,  // 导航类型：驾车
                AmapPageType.ROUTE    // 页面类型：路径规划
            )

            // 启动导航页面
            AmapNaviPage.getInstance().showRouteActivity(
                applicationContext,
                naviParams,
                null  // 导航监听器
            )

        } catch (e: Exception) {
            Toast.makeText(this, "启动导航失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    fun search(goat: String){
        // 1. 创建查询对象
        val query = PoiSearch.Query(goat, "", "广西")
        query.pageSize = 5 // 设置每页返回数量

        // 2. 初始化PoiSearch对象并设置监听
        val poiSearch = PoiSearch(this, query)
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


}