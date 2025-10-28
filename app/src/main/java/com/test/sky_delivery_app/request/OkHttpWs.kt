package com.test.sky_delivery_app.websocket

import android.util.Log
import com.test.sky_delivery_app.pojo.MassageDTO
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

class OkHttpWebSocketService(MsgCallback: (MassageDTO)-> Unit) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

    // WebSocket 监听器
    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.d("WebSocket", "连接已打开")
            // 连接建立后的操作
            webSocket.send("Hello Server!")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            val jsonText = JSONObject(text)
            val massage = MassageDTO(
                jsonText.get("orderId").toString().toLong(),
                jsonText.get("type").toString().toInt(),
                jsonText.get("content").toString(),
                jsonText.get("employeeId").toString().toLong()
            )
            MsgCallback(massage)
            Log.d("WebSocket", "收到消息: $text")
            // 处理文本消息
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
            Log.d("WebSocket", "收到二进制消息: ${bytes.hex()}")
            // 处理二进制消息
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            Log.d("WebSocket", "连接关闭中: $code - $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.d("WebSocket", "连接已关闭: $code - $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.e("WebSocket", "连接失败: ${t.message}")
            // 处理连接失败，可以实现重连逻辑
        }
    }

    fun connect(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(message: String,): Boolean {
        return webSocket?.send(message) ?: false
    }

    fun close() {
        webSocket?.close(1000, "正常关闭")
        client.dispatcher.executorService.shutdown()
    }
}