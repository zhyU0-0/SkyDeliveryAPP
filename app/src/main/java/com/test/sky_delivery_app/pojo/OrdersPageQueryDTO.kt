package com.test.sky_delivery_app.pojo

import java.io.Serializable
import java.time.LocalDateTime

data class OrdersPageQueryDTO(
    var page: Int = 0,
    var pageSize: Int = 0,
    var number: String? = null,
    var phone: String? = null,
    var status: Int? = null,


    var beginTime: LocalDateTime? = null,

    var endTime: LocalDateTime? = null,

    var userId: Long? = null,

    var employeeId: Long? = null


) : Serializable