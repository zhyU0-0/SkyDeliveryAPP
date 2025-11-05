package com.test.sky_delivery_app.pojo.response

import com.test.sky_delivery_app.pojo.Orders

data class BaseResponse (
    val code: Int,
    val msg: String,
    val data: Object?
)