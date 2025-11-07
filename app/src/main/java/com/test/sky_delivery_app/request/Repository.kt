package com.test.sky_delivery_app.request

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.test.sky_delivery_app.pojo.response.LoginData
import com.test.sky_delivery_app.pojo.response.LoginRequest
import com.test.sky_delivery_app.pojo.response.LoginResult
import com.test.sky_delivery_app.pojo.OrderRecord
import com.test.sky_delivery_app.pojo.OrderResponse
import com.test.sky_delivery_app.pojo.Orders
import com.test.sky_delivery_app.pojo.OrdersPageQueryDTO
import com.test.sky_delivery_app.pojo.response.AddressBook
import com.test.sky_delivery_app.pojo.vo.DetailOrderVO
import org.json.JSONArray
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

            if (response.code == 1) {
                val orderResponse = response.data
                orderResponse.records
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
            if(response.code == 1){ 1 }else{ -1 }
        }catch (e: Exception){
            Log.e("delivery",e.toString())
            -1
        }
    }

    suspend fun complete(id:Int):Int{
        return try{
            val response = apiService.complete(id)
            if(response.code == 1){ 1 }else{ -1 }
        }catch (e: Exception){
            Log.e("complete",e.toString())
            -1
        }
    }

    suspend fun getData():List<Orders>{
        return try {
            val response = apiService.getData()
            if(response.code==1){
                response.data
            }else{
                listOf()
            }
        }catch (e: Exception){
            Log.e("getData",e.toString())
            listOf()
        }
    }

    suspend fun getOrderById(id:Int):DetailOrderVO{
        return try {
            val response = apiService.getDetail(id)
            if(response.code==1){
                Log.e("getData",response.data.toString())

                response.data

            }else{
                DetailOrderVO(Orders(),listOf())
            }
        }catch (e: Exception){
            Log.e("getData",e.toString())
            DetailOrderVO(Orders(),listOf())
        }
    }

    suspend fun getAddress(id:Int): AddressBook{
        return try{
            val response = apiService.getAddress(id)
            if(response.code == 1){
                response.data
            }
            AddressBook()
        }catch (e: Exception){
            Log.e("getAddress",e.toString())
            AddressBook()
        }
    }

}