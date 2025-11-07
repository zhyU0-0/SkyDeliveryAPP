package com.test.sky_delivery_app.pojo.response

data class AddressResponse (
    val code: Int,
    val msg: String,
    val data: AddressBook
)

data class AddressBook(
    val id: Long? = null,
    val userId: Long? = null,
    val consignee: String? = null,
    val phone: String? = null,
    val sex: String? = null,
    val provinceCode: String? = null,
    val provinceName: String? = null,
    val cityCode: String? = null,
    val cityName: String? = null,
    val districtCode: String? = null,
    val districtName: String? = null,
    val detail: String? = null,
    val label: String? = null,
    val isDefault: Int? = null
)