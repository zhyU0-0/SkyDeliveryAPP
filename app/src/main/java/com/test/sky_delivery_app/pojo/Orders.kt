package com.test.sky_delivery_app.pojo

import com.test.sky_delivery_app.pojo.response.AddressBook

data class Orders(
    var id: Long? = null,
    var number: String? = null,
    var status: Int? = null,
    var userId: Long? = null,
    var addressBookId: Long? = null,
    var orderTime: String? = null,
    var checkoutTime: String? = null,
    var payMethod: Int? = null,
    var payStatus: Int? = null,
    var amount: Double = 0.0,
    var remark: String? = null,
    var userName: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var consignee: String? = null,
    var cancelReason: String? = null,
    var rejectionReason: String? = null,
    var cancelTime: String? = null,
    var estimatedDeliveryTime: String? = null,
    var deliveryStatus: Int? = null,
    var deliveryTime: String? = null,
    var packAmount: Int? = null,
    var tablewareNumber: Int? = null,
    var tablewareStatus: Int? = null,
    var employeeId: Long? = null
)