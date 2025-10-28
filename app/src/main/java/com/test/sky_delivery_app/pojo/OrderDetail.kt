package com.test.sky_delivery_app.pojo


data class OrderDetail(
    val id: Long,
    val name: String,
    val orderId: Long,
    val dishId: Long?,
    val setmealId: Long?,
    val dishFlavor: String,
    val number: Int,
    val amount: Double,
    val image: String
)