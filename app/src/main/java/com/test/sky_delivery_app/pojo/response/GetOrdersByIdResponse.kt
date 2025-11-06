package com.test.sky_delivery_app.pojo.response

import com.test.sky_delivery_app.pojo.vo.DetailOrderVO

data class GetOrdersByIdResponse(
    val code: Int,
    val msg: String,
    val data: DetailOrderVO
)


