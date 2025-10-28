package com.test.sky_delivery_app.pojo

data class MassageDTO (
    val orderId: Long,
    val type: Int,
    val content: String,
    val employeeId:Long
)
