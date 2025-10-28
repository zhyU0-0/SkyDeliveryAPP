package com.test.sky_delivery_app.pojo


data class OrderRecord(
    val id: Long,
    val number: String,
    val status: Int,
    val userId: Long,
    val addressBookId: Long,
    val orderTime: String,
    val checkoutTime: String,
    val payMethod: Int,
    val payStatus: Int,
    val amount: Double,
    val remark: String?,
    val userName: String?,
    val phone: String,
    val address: String,
    val consignee: String,
    val cancelReason: String?,
    val rejectionReason: String?,
    val cancelTime: String?,
    val estimatedDeliveryTime: String,
    val deliveryStatus: Int,
    val deliveryTime: String?,
    val packAmount: Int,
    val tablewareNumber: Int,
    val tablewareStatus: Int,
    val orderDishes: String?,
    val orderDetailList: List<OrderDetail>
)
