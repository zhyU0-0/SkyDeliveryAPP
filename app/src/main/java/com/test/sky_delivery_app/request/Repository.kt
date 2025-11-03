package com.test.sky_delivery_app.request

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.test.sky_delivery_app.pojo.LoginData
import com.test.sky_delivery_app.pojo.LoginRequest
import com.test.sky_delivery_app.pojo.LoginResult
import com.test.sky_delivery_app.pojo.OrderRecord
import com.test.sky_delivery_app.pojo.OrderResponse
import com.test.sky_delivery_app.pojo.Orders
import com.test.sky_delivery_app.pojo.OrdersPageQueryDTO
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Repository(
    private val apiService: ApiService,
    private val sharedPreferences: SharedPreferences
) {
    val gson = Gson()
    // 使用协程的挂起函数版本
    suspend fun login(userName: String, password: String): LoginResult {
        return try {
            val request = LoginRequest(userName, password)
            val response = apiService.login(request)

            if (response.code == 200 || response.code == 1) { // 根据你的API调整成功码
                // 保存到 SharedPreferences
                saveUserData(response.data)
                LoginResult.Success(response.data)
            } else {
                LoginResult.Error(response.msg ?: "登录失败", response.code)
            }
        } catch (e: IOException) {
            LoginResult.NetworkError
        } catch (e: Exception) {
            LoginResult.Error("登录失败: ${e.message}")
        }
    }


    private fun saveUserData(loginData: LoginData?) {
        if(loginData != null){
            sharedPreferences.edit()
                .putString("token", loginData.token)
                .putLong("cId", loginData.id)
                .putString("name", loginData.name)
                .putString("username", loginData.userName)
                .apply()
        }

    }

    suspend fun getOrders(op: OrdersPageQueryDTO): List<OrderRecord> {
        return try {
            val response = apiService.getOrders(
                page = op.page,
                pageSize = op.pageSize,
                status = op.status,
                employeeId = op.employeeId
            )

            if (response.isSuccessful) {
                val body = response.body.toString()
                if (body.isNotEmpty()) {
                    val responseJson = JSONObject(body)

                    val jsonObject = JSONObject(responseJson.toString())
                    val code = jsonObject.getInt("code")
                    val msg = jsonObject.optString("msg")

                    val dataObject = jsonObject.getJSONObject("data")
                    Log.v("OrderVOPage",dataObject.toString())
                    val orderResponse = gson.fromJson(dataObject.toString(), OrderResponse::class.java)

                    orderResponse.records
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("getOrders",e.toString())
            emptyList()
        }
    }

    suspend fun delivery(id:Int):Int{
        return try{
            val response = apiService.delivery(id)
            if(response.isSuccessful){ 1 }else{ -1 }
        }catch (e: Exception){
            Log.e("delivery",e.toString())
            -1
        }
    }

    suspend fun complete(id:Int):Int{
        return try{
            val response = apiService.complete(id)
            if(response.isSuccessful){ 1 }else{ -1 }
        }catch (e: Exception){
            Log.e("complete",e.toString())
            -1
        }
    }

    suspend fun getData():List<Orders>{
        return try {
            val response = apiService.getData()
            if(response.isSuccessful){
                val jsonData = JSONObject(response.body.toString())
                val data = jsonData.getJSONArray("data")
                val orderList = mutableListOf<Orders>()
                if(data.length() > 0){
                    var index = 0
                    while (index < data.length()){
                        Log.v("Orders",data.getJSONObject(index).toString())
                        val orderJson = data.getJSONObject(index).toString()
                        val od = gson.fromJson(orderJson, Orders::class.java)
                        orderList.add(od)
                        index++
                    }
                }
                orderList
            }else{
                listOf()
            }
        }catch (e: Exception){
            Log.e("getData",e.toString())
            listOf()
        }
    }

}