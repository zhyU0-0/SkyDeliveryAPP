package com.test.sky_delivery_app.pojo

data class OrderResponse(
    val total: Int,
    val records: List<OrderRecord>
)