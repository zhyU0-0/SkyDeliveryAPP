package com.test.sky_delivery_app.websocket

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.test.sky_delivery_app.pojo.MassageDTO
import com.test.sky_delivery_app.pojo.OrderRecord
import com.test.sky_delivery_app.pojo.OrderResponse
import com.test.sky_delivery_app.pojo.Orders
import com.test.sky_delivery_app.pojo.OrdersPageQueryDTO
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

class OkHttpController(private val context: Context, private val sharedPreferences: SharedPreferences,msgCallback: (MassageDTO)->Unit) {
    private val client = OkHttpClient()
    val okHttpWebSocketService = OkHttpWebSocketService(msgCallback)

    val baseUrl = "http://10.0.2.2:8080" // 替换为你的实际 URL

    fun connectWS() {
        val empId = sharedPreferences.getLong("cId",0)
        okHttpWebSocketService.connect("ws://10.0.2.2:8080/ws/${empId}") // WebSocket URL
    }
    fun close(){
        okHttpWebSocketService.close()
    }

    // 通用的 GET 请求方法
    fun asyncGet(url: String, callback: (Result<String>) -> Unit): Call {
        val request = Request.Builder()
            .url(url)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {

                    val responseJson = JSONObject(body)

                    val jsonObject = JSONObject(responseJson.toString())
                    val code = jsonObject.getInt("code")
                    val msg = jsonObject.optString("msg")

                    val dataObject = jsonObject.getJSONObject("data")


                    callback(Result.success(dataObject.toString()))
                } else {
                    callback(Result.failure(IOException("请求失败: ${response.code}")))
                }
            }
        })

        return call
    }

    // POST 登录方法 - 使用 JSON 格式
    fun login(userName: String, password: String, callback: (Result<String>) -> Unit): Call {
        // 构建 JSON 请求体
        val jsonBody = """
            {
                "username": "$userName",
                "password": "$password"
            }
        """.trimIndent()

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            jsonBody
        )

        val request = Request.Builder()
            .url("$baseUrl/admin/employee/login") // 登录接口
            .post(requestBody) // 指定 POST 方法
            .addHeader("Content-Type", "application/json")
            .build()

        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.v("OKHttp",body.toString())
                if (response.isSuccessful && body != null) {
                    val responseJson = JSONObject(body)

                    try {
                        val jsonObject = JSONObject(responseJson.toString())
                        val code = jsonObject.getInt("code")
                        val msg = jsonObject.optString("msg")

                        val dataObject = jsonObject.getJSONObject("data")

                        val token = dataObject.getString("token")
                        val userName = dataObject.getString("userName")
                        val name = dataObject.getString("name")
                        val id = dataObject.getLong("id")

                        sharedPreferences.edit()
                            .putString("token",token)
                            .putLong("cId",id)
                            .putString("name",name)
                            .putString("username",userName)
                            .apply()


                        Log.v("OKHttp",body)
                        callback(Result.success(body))

                    }catch (e: Error){
                        callback(Result.success(body))
                    }

                } else {
                    callback(Result.failure(IOException("登录失败: ${response.code} - ${response.message}")))
                }
            }
        })

        return call
    }

    // 通用 POST 方法 - JSON 格式
    fun postJson(url: String, jsonData: String, callback: (Result<String>) -> Unit): Call {
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(), jsonData)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    callback(Result.success(body))
                } else {
                    callback(Result.failure(IOException("POST 请求失败: ${response.code}")))
                }
            }
        })

        return call
    }

    // POST 方法 - 表单格式
    fun postForm(url: String, formData: Map<String, String>, callback: (Result<String>) -> Unit): Call {
        val formBodyBuilder = FormBody.Builder()
        formData.forEach { (key, value) ->
            formBodyBuilder.add(key, value)
        }

        val request = Request.Builder()
            .url(url)
            .post(formBodyBuilder.build())
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    callback(Result.success(body))
                } else {
                    callback(Result.failure(IOException("POST 请求失败: ${response.code}")))
                }
            }
        })

        return call
    }

    // 带认证头的 POST 请求
    fun postWithAuth(url: String, jsonData: String, token: String, callback: (Result<String>) -> Unit): Call {
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaType(),jsonData)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    callback(Result.success(body))
                } else {
                    callback(Result.failure(IOException("请求失败: ${response.code}")))
                }
            }
        })

        return call
    }

    fun getOrders(op: OrdersPageQueryDTO, callback: (List<OrderRecord>) -> Unit){
        val token = sharedPreferences.getString("token","null").toString()
        val request = Request.Builder()
            .url("$baseUrl/deliver/order/conditionSearch?page=${op.page}&pageSize=${op.pageSize}" +
                    "&status=${op.status}${if (op.employeeId == null) "" else "&employeeId="+op.employeeId.toString()}")
            .addHeader("token",token)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val gson = Gson()
                if (response.isSuccessful && body != null) {

                    val responseJson = JSONObject(body)

                    val jsonObject = JSONObject(responseJson.toString())
                    val code = jsonObject.getInt("code")
                    val msg = jsonObject.optString("msg")

                    val dataObject = jsonObject.getJSONObject("data")
                    Log.v("OrderVOPage",dataObject.toString())
                    val orderResponse = gson.fromJson(dataObject.toString(), OrderResponse::class.java)
                    callback(orderResponse.records)
                } else {
                    callback(emptyList())
                }
            }
        })

        callback(emptyList())
    }

    fun delivery(id:Int,callback: (String)-> Unit){

        val request = Request.Builder()
            .url(baseUrl+"/deliver/order/delivery/"+id.toString())
            .put(RequestBody.create("application/json; charset=utf-8".toMediaType(),""))
            .addHeader("token",sharedPreferences.getString("token","").toString())
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                callback(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.code.toString())//200
                } else {
                    callback("fail")
                }
            }
        })

    }

    fun complete(id:Int,callback: (String)-> Unit){

        val request = Request.Builder()
            .url(baseUrl+"/deliver/order/complete/"+id.toString())
            .put(RequestBody.create("application/json; charset=utf-8".toMediaType(),""))
            .addHeader("token",sharedPreferences.getString("token","").toString())
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                callback(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.code.toString())//200
                } else {
                    callback("fail")
                }
            }
        })

    }

    fun getData(callback:(List<Orders>)->Unit){

        val request = Request.Builder()
            .url(baseUrl+"/deliver/report/data")
            .get()
            .addHeader("token",sharedPreferences.getString("token","").toString())
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.v("get Data fail",e.toString())
            }

            override fun onResponse(call: Call, response: Response){
                val body = response.body?.string()
                val gson = Gson()
                if (response.isSuccessful && body != null) {

                    val responseJson = JSONObject(body)

                    val jsonObject = JSONObject(responseJson.toString())
                    val code = jsonObject.getInt("code")
                    val msg = jsonObject.optString("msg")

                    val dataObject = jsonObject.getJSONArray("data")
                    val orders: MutableList<Orders> = mutableListOf()
                    if (dataObject.length() > 0) {
                        var index = 0
                        while(index < dataObject.length()){
                            Log.v("OrderVOPage",dataObject.getJSONObject(index).toString())
                            val data = dataObject.getJSONObject(index)
                            val or = gson.fromJson(data.toString(), Orders::class.java)
                            orders.add(or)
                            index++
                        }
                    }
                    callback(orders)
                } else {
                    callback(listOf())
                }
            }

        })
    }
}
