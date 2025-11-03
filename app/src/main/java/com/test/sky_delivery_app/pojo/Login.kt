package com.test.sky_delivery_app.pojo

// 登录请求体
data class LoginRequest(
    val username: String,
    val password: String
)

// 登录响应数据
data class LoginResponse(
    val code: Int,
    val msg: String,
    val data: LoginData?
)

// 登录成功返回的数据
data class LoginData(
    val token: String,
    val userName: String,
    val name: String,
    val id: Long
)

// 登录结果密封类
sealed class LoginResult {
    data class Success(val data: LoginData?) : LoginResult()
    data class Error(val message: String, val code: Int? = null) : LoginResult()
    object NetworkError : LoginResult()
}