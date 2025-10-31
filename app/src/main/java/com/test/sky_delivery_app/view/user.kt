package com.test.sky_delivery_app.view

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.sky_delivery_app.QR.QRCodeScanner
import com.test.sky_delivery_app.R
import com.test.sky_delivery_app.viewmodel.HttpViewModel

@Composable
fun UserScreen(viewModel: HttpViewModel){
    val context = LocalContext.current
    val employee = remember { viewModel._getEmployee() }
    Column {
        Row {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "图片描述",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Column {
                Text(employee.name, fontSize = 30.sp)
                Text(employee.username, fontSize = 10.sp, color = Color.Gray)
            }
        }

        Button(
            onClick = {
                val intent = Intent(context, QRActivity::class.java)
                context.startActivity(intent)
            }
        ) {

        }
    }
}