package com.test.sky_delivery_app.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.test.sky_delivery_app.MainActivity
import com.test.sky_delivery_app.view.ui.theme.SkyDeliveryAppTheme
import com.test.sky_delivery_app.viewmodel.HttpViewModel
import com.test.sky_delivery_app.websocket.OkHttpController
import okhttp3.Callback

class LoginActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var okHttpController: OkHttpController
    private lateinit var wsViewModel: HttpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        wsViewModel = HttpViewModel(this,sharedPreferences)
        okHttpController = wsViewModel.okHttpController
        enableEdgeToEdge()
        setContent {
            SkyDeliveryAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        wsViewModel,
                        {
                            val intent = Intent(this, MainActivity::class.java)
                            this.startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        wsViewModel.destroy()
    }
}

@Composable
fun LoginScreen( modifier: Modifier = Modifier,viewModel: HttpViewModel,
                 goto: ()-> Unit) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("123456") }
    val context = LocalContext.current
    Column {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("username") },
            placeholder = { Text("username") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("password") },
            placeholder = { Text("password") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                viewModel.login(
                    username,
                    password,
                    {
                        /*val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)*/
                    }
                )
                goto()

            }
        ) { }
    }
}

