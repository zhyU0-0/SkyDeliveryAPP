package com.test.sky_delivery_app.QR

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ScanningOverlay() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val boxSize = 250.dp.toPx()

        // 半透明背景
        drawRect(
            color = Color.Black.copy(alpha = 0.4f),
            size = size
        )

        // 透明扫描框区域
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(
                x = (canvasWidth - boxSize) / 2,
                y = (canvasHeight - boxSize) / 2
            ),
            size = Size(boxSize, boxSize)
        )

        // 扫描框边框
        drawRect(
            color = Color.Green,
            topLeft = Offset(
                x = (canvasWidth - boxSize) / 2,
                y = (canvasHeight - boxSize) / 2
            ),
            size = Size(boxSize, boxSize),
            style = Stroke(width = 3.dp.toPx())
        )

        // 扫描框四角
        val cornerLength = 20.dp.toPx()
        val strokeWidth = 5.dp.toPx()

        // 左上角
        drawLine(
            color = Color.Green,
            start = Offset(
                x = (canvasWidth - boxSize) / 2,
                y = (canvasHeight - boxSize) / 2
            ),
            end = Offset(
                x = (canvasWidth - boxSize) / 2 + cornerLength,
                y = (canvasHeight - boxSize) / 2
            ),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.Green,
            start = Offset(
                x = (canvasWidth - boxSize) / 2,
                y = (canvasHeight - boxSize) / 2
            ),
            end = Offset(
                x = (canvasWidth - boxSize) / 2,
                y = (canvasHeight - boxSize) / 2 + cornerLength
            ),
            strokeWidth = strokeWidth
        )

        // 右上角
        drawLine(
            color = Color.Green,
            start = Offset(
                x = (canvasWidth + boxSize) / 2,
                y = (canvasHeight - boxSize) / 2
            ),
            end = Offset(
                x = (canvasWidth + boxSize) / 2 - cornerLength,
                y = (canvasHeight - boxSize) / 2
            ),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.Green,
            start = Offset(
                x = (canvasWidth + boxSize) / 2,
                y = (canvasHeight - boxSize) / 2
            ),
            end = Offset(
                x = (canvasWidth + boxSize) / 2,
                y = (canvasHeight - boxSize) / 2 + cornerLength
            ),
            strokeWidth = strokeWidth
        )

        // 左下角
        drawLine(
            color = Color.Green,
            start = Offset(
                x = (canvasWidth - boxSize) / 2,
                y = (canvasHeight + boxSize) / 2
            ),
            end = Offset(
                x = (canvasWidth - boxSize) / 2 + cornerLength,
                y = (canvasHeight + boxSize) / 2
            ),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.Green,
            start = Offset(
                x = (canvasWidth - boxSize) / 2,
                y = (canvasHeight + boxSize) / 2
            ),
            end = Offset(
                x = (canvasWidth - boxSize) / 2,
                y = (canvasHeight + boxSize) / 2 - cornerLength
            ),
            strokeWidth = strokeWidth
        )

        // 右下角
        drawLine(
            color = Color.Green,
            start = Offset(
                x = (canvasWidth + boxSize) / 2,
                y = (canvasHeight + boxSize) / 2
            ),
            end = Offset(
                x = (canvasWidth + boxSize) / 2 - cornerLength,
                y = (canvasHeight + boxSize) / 2
            ),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = Color.Green,
            start = Offset(
                x = (canvasWidth + boxSize) / 2,
                y = (canvasHeight + boxSize) / 2
            ),
            end = Offset(
                x = (canvasWidth + boxSize) / 2,
                y = (canvasHeight + boxSize) / 2 - cornerLength
            ),
            strokeWidth = strokeWidth
        )
    }
}