package com.test.sky_delivery_app.pojo.response

import com.test.sky_delivery_app.pojo.OrderResponse

class GetOrdersResponse (
    val code: Int,
    val msg: String,
    val data: OrderResponse
)
