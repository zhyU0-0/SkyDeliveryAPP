package com.test.sky_delivery_app.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.maps.AMap
import com.amap.api.maps.MapView
import com.amap.api.maps.model.MyLocationStyle
import com.test.sky_delivery_app.R

class MapActivity : Activity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private var mMapView: MapView? = null
    private var aMap: AMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // 先初始化地图视图
        mMapView = findViewById(R.id.map)
        mMapView?.onCreate(savedInstanceState)
        aMap = mMapView?.map

        // 然后检查权限
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已经有权限，初始化定位
                initLocation()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // 用户之前拒绝过，解释为什么需要权限
                Toast.makeText(this, "需要位置权限来显示您的位置", Toast.LENGTH_LONG).show()
                requestLocationPermission()
            }
            else -> {
                // 第一次请求权限
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被授予，初始化定位
                    initLocation()
                } else {
                    // 权限被拒绝
                    Toast.makeText(this, "位置权限被拒绝，无法显示定位", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initLocation() {
        aMap?.let { map ->
            try {
                val myLocationStyle = MyLocationStyle().apply {
                    myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) // 定位一次，并将镜头移动到地图中心点
                    interval(2000) // 设置定位间隔
                    showMyLocation(true) // 显示定位蓝点
                }

                map.apply {
                    setMyLocationStyle(myLocationStyle)
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true // 显示定位按钮
                }

                // 添加地图加载监听，确保地图完全加载后再进行定位
                map.setOnMapLoadedListener {
                    Toast.makeText(this@MapActivity, "地图加载完成，开始定位", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "定位初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 检查定位服务是否开启
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 禁用定位
        aMap?.isMyLocationEnabled = false
        mMapView?.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
        // 重新检查定位状态
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            aMap?.isMyLocationEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
        // 暂停时禁用定位以节省资源
        aMap?.isMyLocationEnabled = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState)
    }
}