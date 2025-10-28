package com.test.sky_delivery_app.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.test.sky_delivery_app.viewmodel.HttpViewModel

@Composable
fun UserScreen(viewModel: HttpViewModel){
    Column {
        Text("User")
    }
}