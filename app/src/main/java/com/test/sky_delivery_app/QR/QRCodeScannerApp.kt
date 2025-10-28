package com.test.sky_delivery_app.QR

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun QRCodeScannerApp() {
    var result by remember { mutableStateOf("") }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "scanner"
    ) {
        composable("scanner") {
            Text(result, fontSize = 20.sp)
            ScannerScreen(
                result = result,
                getResult = {newResult->
                    result = newResult
                }
            )
        }
    }
}