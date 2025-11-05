package com.test.sky_delivery_app.request

import com.test.sky_delivery_app.pojo.response.BaseResponse
import com.test.sky_delivery_app.pojo.response.GetDataResponse
import com.test.sky_delivery_app.pojo.response.GetOrdersResponse
import com.test.sky_delivery_app.pojo.response.LoginRequest
import com.test.sky_delivery_app.pojo.response.LoginResponse
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /*// GET 请求 - 基本用法
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User

    // GET 请求 - 带查询参数
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ): List<User>

    // POST 请求 - 发送 JSON 数据
    @POST("users")
    suspend fun createUser(@Body user: User): User

    // PUT 请求 - 更新数据
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: User
    ): User

    // DELETE 请求
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>

    // 多路径参数
    @GET("users/{userId}/posts/{postId}")
    suspend fun getUserPost(
        @Path("userId") userId: Int,
        @Path("postId") postId: Int
    ): Post

    // 动态 URL
    @GET
    suspend fun getDynamicUrl(@Url url: String): User*/

    @POST("admin/employee/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("deliver/order/conditionSearch")
    suspend fun getOrders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("status") status: Int?,
        @Query("employeeId") employeeId: Long?
    ): GetOrdersResponse

    @PUT("deliver/order/delivery/{id}")
    suspend fun delivery(@Path("id") id:Int): BaseResponse

    @PUT("deliver/order/complete/{id}")
    suspend fun complete(@Path("id") id:Int): BaseResponse

    @GET("deliver/report/data")
    suspend fun getData(): GetDataResponse


}