package com.test.sky_delivery_app

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.amap.api.location.AMapLocationClient
import com.test.sky_delivery_app.request.RetrofitClient
import com.test.sky_delivery_app.ui.theme.SkyDeliveryAppTheme
import com.test.sky_delivery_app.viewmodel.HttpViewModel
import com.test.sky_delivery_app.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var wsViewModel: HttpViewModel
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = this.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        wsViewModel = HttpViewModel(this,sharedPreferences)
        mapViewModel = MapViewModel(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(wsViewModel,mapViewModel)
        }
        wsViewModel.load()

        mapInit()

    }


    override fun onDestroy() {
        super.onDestroy()
        wsViewModel.destroy()
    }
    private fun mapInit() {
        initAMapPrivacy()//SDK设置
        checkPermissions()//权限监测

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
        val missingPermissions = mapViewModel.requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                mapViewModel.PERMISSION_REQUEST_CODE
            )
        } else {
            Toast.makeText(this, "需要权限才能使用导航功能", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == mapViewModel.PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                /*initMap()*/
            } else {
                Toast.makeText(this, "需要位置权限才能使用导航功能", Toast.LENGTH_LONG).show()
            }
        }
    }
}
