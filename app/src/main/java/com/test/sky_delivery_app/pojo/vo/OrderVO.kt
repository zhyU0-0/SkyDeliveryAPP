package com.test.sky_delivery_app.pojo.vo


data class OrderVO(
    val id: Long,
    val number: String,
    val userId: Long,
    val addressBookId: Long,
    val checkoutTime: String,
    val amount: Double,
    val remark: String?,
    val userName: String?,
    val phone: String,
    val address: String,
)
