package com.test.sky_delivery_app.view

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.test.sky_delivery_app.QR.QRCodeScanner
import com.test.sky_delivery_app.viewmodel.HttpViewModel

@Composable
fun UserScreen(viewModel: HttpViewModel){
    val context = LocalContext.current
    Column {
        Text("User")
        Button(
            onClick = {
                val intent = Intent(context, QRActivity::class.java)
                context.startActivity(intent)
            }
        ) { }
    }
}