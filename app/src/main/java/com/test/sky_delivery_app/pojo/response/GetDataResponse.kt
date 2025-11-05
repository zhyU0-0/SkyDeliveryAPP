package com.test.sky_delivery_app.pojo.response

import com.test.sky_delivery_app.pojo.Orders


data class GetDataResponse(
    val code: Int,
    val msg: String,
    val data: List<Orders>
)