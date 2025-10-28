package com.test.sky_delivery_app.QR
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@Composable
fun ScannerScreen(
    result: String,
    getResult:(String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(hasCameraPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var isScanning by remember { mutableStateOf(true) }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    var showResultDialog by remember { mutableStateOf(false) }

    // 处理扫描结果
    LaunchedEffect(scannedResult) {
        scannedResult?.let {
            showResultDialog = true
            isScanning = false
        }
    }

    // 权限检查
    if (!hasCameraPermission) {
        PermissionRequestScreen(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )
    } else {
        MainScannerContent(
            isScanning = isScanning,
            scannedResult = scannedResult,
            showResultDialog = showResultDialog,
            onScanResult = { result ->
                scannedResult = result
                getResult(result)
            },
            onContinueScanning = {
                scannedResult = null
                isScanning = true
                showResultDialog = false
            },
            onToggleScanning = {
                isScanning = !isScanning
            }
        )
    }
}
// 检查相机权限的辅助函数
private fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
@Composable
fun MainScannerContent(
    isScanning: Boolean,
    scannedResult: String?,
    showResultDialog: Boolean,
    onScanResult: (String) -> Unit,
    onContinueScanning: () -> Unit,
    onToggleScanning: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 二维码扫描器
        QRCodeScanner(
            onQRCodeScanned = onScanResult,
            isScanningEnabled = isScanning
        )

        // 控制按钮
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 扫描状态指示
            Text(
                text = if (isScanning) "扫描中..." else "扫描已暂停",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // 控制按钮
            Button(
                onClick = onToggleScanning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanning) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isScanning) Icons.Default.Refresh
                    else Icons.Default.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isScanning) "停止扫描" else "开始扫描")
            }
        }

        // 结果对话框
        if (showResultDialog) {
            ResultDialog(
                result = scannedResult ?: "",
                onContinue = onContinueScanning,
                onDismiss = {
                    // 使用 lambda 表达式来修改 showResultDialog
                    // 这里不需要重新赋值，因为 onDismiss 会被调用
                    // 我们通过参数传递关闭对话框的逻辑
                }
            )
        }
    }
}

@Composable
fun ResultDialog(
    result: String,
    onContinue: () -> Unit,
    onDismiss: () -> Unit  // 这里接收一个回调函数，而不是直接修改变量
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "扫描结果",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = result,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .heightIn(max = 200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("关闭")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onContinue) {
                        Text("继续扫描")
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "需要相机权限",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "此应用需要相机权限来扫描二维码",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermission
        ) {
            Text("请求权限")
        }
    }
}