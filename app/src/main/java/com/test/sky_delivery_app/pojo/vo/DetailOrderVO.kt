package com.test.sky_delivery_app.pojo.vo

import com.test.sky_delivery_app.pojo.OrderDetail
import com.test.sky_delivery_app.pojo.Orders

data class DetailOrderVO(
    val orderDishes: Orders,
    val orderDetailList: List<OrderDetail>
)