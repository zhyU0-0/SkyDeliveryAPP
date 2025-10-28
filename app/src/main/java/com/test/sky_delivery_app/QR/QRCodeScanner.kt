package com.test.sky_delivery_app.QR

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun QRCodeScanner(
    onQRCodeScanned: (String) -> Unit,
    isScanningEnabled: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val barcodeScanner = remember {
        BarcodeScanning.getClient()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    // 预览设置
                    val cameraPreview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    preview = cameraPreview

                    // 图像分析设置
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(
                        cameraExecutor,
                        { imageProxy ->
                            if (isScanningEnabled) {
                                Log.v("isScanning",isScanningEnabled.toString())
                                processImage(
                                    imageProxy = imageProxy,
                                    barcodeScanner = barcodeScanner,
                                    onQRCodeScanned = onQRCodeScanned
                                )
                            } else {
                                imageProxy.close()
                            }
                        }
                    )

                    // 选择后置摄像头
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            cameraPreview,
                            imageAnalysis
                        )
                    } catch (exc: Exception) {
                        Log.e("QRCodeScanner", "相机绑定失败", exc)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // 扫描框叠加层
        ScanningOverlay()
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImage(
    imageProxy: androidx.camera.core.ImageProxy,
    barcodeScanner: BarcodeScanner,
    onQRCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrCode ->
                        when (barcode.valueType) {
                            Barcode.TYPE_URL -> {
                                if(true){
                                    onQRCodeScanned("URL: ${barcode.url?.url}")
                                }

                            }
                            Barcode.TYPE_TEXT -> {
                                onQRCodeScanned("文本: $qrCode")
                            }
                            Barcode.TYPE_WIFI -> {
                                val wifi = barcode.wifi
                                onQRCodeScanned("WiFi: ${wifi?.ssid}")
                            }
                            else -> {
                                onQRCodeScanned(qrCode)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QRCodeScanner", "二维码识别失败", exception)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}