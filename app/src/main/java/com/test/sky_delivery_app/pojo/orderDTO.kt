package com.test.sky_delivery_app.pojo

import kotlinx.serialization.Serializable

@Serializable
data class OrderDTO(
    val orderId: Long,
    val type: Int,
    val content: String
)