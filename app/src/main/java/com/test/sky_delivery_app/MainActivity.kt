package com.test.sky_delivery_app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import com.test.sky_delivery_app.ui.theme.SkyDeliveryAppTheme
import com.test.sky_delivery_app.viewmodel.HttpViewModel
import com.test.sky_delivery_app.websocket.OkHttpController

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var okHttpController: OkHttpController
    private lateinit var wsViewModel: HttpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = this.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        wsViewModel = HttpViewModel(this,sharedPreferences)
        okHttpController = wsViewModel.okHttpController
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(wsViewModel)
        }
        wsViewModel.load()
    }


    override fun onDestroy() {
        super.onDestroy()
        wsViewModel.destroy()
    }
}
