package com.test.sky_delivery_app.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.sky_delivery_app.pojo.Employee
import com.test.sky_delivery_app.pojo.MassageDTO
import com.test.sky_delivery_app.pojo.Orders
import com.test.sky_delivery_app.pojo.OrdersPageQueryDTO
import com.test.sky_delivery_app.pojo.vo.OrderVO
import com.test.sky_delivery_app.request.Repository
import com.test.sky_delivery_app.request.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.find
import kotlin.collections.forEach
import kotlin.collections.minus
import kotlin.collections.plus
import androidx.core.content.edit
import com.test.sky_delivery_app.pojo.response.LoginResult
import com.test.sky_delivery_app.pojo.vo.DetailOrderVO
import com.test.sky_delivery_app.websocket.OkHttpWebSocketService

class HttpViewModel(val context: Context, val shapePreferences: SharedPreferences) : ViewModel() {

    // 使用下划线前缀表示可变的内部状态
    private val _messageList = MutableStateFlow<List<MassageDTO>>(emptyList())
    private val _orderList = MutableStateFlow<List<OrderVO>>(emptyList())
    private val _deliveryList = MutableStateFlow<List<OrderVO>>(emptyList())
    private val _dataList = MutableStateFlow<List<Orders>>(emptyList())
    private val _completeList = MutableStateFlow<List<OrderVO>>(emptyList())
    // 公开的不可变状态
    val messageList: StateFlow<List<MassageDTO>> = _messageList.asStateFlow()
    val orderList: StateFlow<List<OrderVO>> = _orderList.asStateFlow()
    val deliveryList: StateFlow<List<OrderVO>> = _deliveryList.asStateFlow()
    val dataList: StateFlow<List<Orders>> = _dataList.asStateFlow()
    val complete: StateFlow<List<OrderVO>> = _completeList.asStateFlow()

    val delivery_succeeful = mutableStateOf(false)
    val complete_succeeful = mutableStateOf(false)
    var is_show_cpList = mutableStateOf(false)
    lateinit var employee: Employee
    var orderMoney = mutableStateOf(0.0)
    var orderCount = mutableStateOf(0)
    var detail = mutableStateOf(DetailOrderVO(Orders(),listOf()))

    val okHttpWebSocketService = OkHttpWebSocketService({
            massageDTO ->
        if(shapePreferences.getLong("cId",0)==massageDTO.employeeId){
            _messageList.update { currentList ->
                listOf(massageDTO) + currentList
            }
        }
        Log.v("The Message", massageDTO.toString())
        Log.v("MessageList",_messageList.toString())
    })

    private val authRepository = Repository(
        RetrofitClient.authApiService,
        shapePreferences
    )



    //val okHttpController =  OkHttpController(context,shapePreferences,)

    fun _getEmployee(): Employee{
        employee = Employee(
            shapePreferences.getLong("cId",0),
            shapePreferences.getString("name","null").toString(),
            shapePreferences.getString("username","null").toString()
        )
        return employee
    }

    fun is_auth(callback: ()-> Unit,fail:()->Unit,finish:()->Unit){
        if(shapePreferences.getString("password","null").toString() != "null"){
            //如果密码不为空，先进入
            callback()
            viewModelScope.launch {

                val result = authRepository.login(
                    shapePreferences.getString("username","null").toString(),
                    shapePreferences.getString("password","null").toString(),
                )
                when (result) {
                    is LoginResult.Error -> {
                        Log.v("Error","Error")
                        Log.v("Login","fail")
                        fail()//登录失败再回来
                    }
                    LoginResult.NetworkError -> {
                        Log.v("NetworkError","NetworkError")
                        Toast.makeText(context,"服务器未连接",Toast.LENGTH_SHORT).show()
                    }
                    is LoginResult.Success -> {
                        finish()
                        getOrder()//起身份验证作用
                        getDeliveryOrder()
                    }
                }
            }

        }else{
            Log.v("Login","password not exist sharePreference ")
        }
    }

    fun login(userName: String, password: String, callback: () -> Unit) {
        //登录后再次验证身份，方便获取employeeId

        viewModelScope.launch {
            val result = authRepository.login(userName, password)
            when (result) {
                is LoginResult.Error -> {
                    Log.v("Error","Error")
                }
                LoginResult.NetworkError -> {
                    Log.v("NetworkError","NetworkError")
                }
                is LoginResult.Success -> {
                    shapePreferences.edit {
                            putString("password", password)
                        }
                    callback()
                    getOrder()//起身份验证作用
                    getDeliveryOrder()
                }
            }
        }
    }

    fun confine(id:Long){
        Log.d("deleteMsg",id.toString())
        val massageDTO = _messageList.value.find {it.orderId == id}
        val msg = MassageDTO(
            id,
            massageDTO?.type ?: 3,
            massageDTO?.content ?: "",
            massageDTO?.employeeId ?: 0,
        )
        _messageList.update { currentList ->
            currentList - msg
        }
        Log.d("MsgList",_messageList.value.size.toString())
    }
    fun load(){
        val empId = shapePreferences.getLong("cId",0)
        val url = "ws://" + shapePreferences.getString("ip", "10.0.2.2:8088") + "/"
        okHttpWebSocketService.connect(url+"ws/${empId}") // WebSocket URL

        var sum = 0.0
        dataList.value.forEach { or->
            sum += or.amount*0.1
        }
        orderMoney.value = sum
        orderCount.value = dataList.value.size
    }

    fun send(){
        okHttpWebSocketService.sendMessage("")
    }

    fun destroy(){
        okHttpWebSocketService.close()
    }
    
    fun getOrder(){

        val op = OrdersPageQueryDTO(1,100,null,null,3,null,null,null,null)//3->4:店家接单->派送中

        viewModelScope.launch {
            val result = authRepository.getOrders(op)
            val orderVoList = mutableListOf<OrderVO>()
            result.forEach { or->
                orderVoList.add(OrderVO(
                    id = or.id,
                    number = or.number,
                    userId = or.userId,
                    phone = or.phone,
                    addressBookId = or.addressBookId,
                    checkoutTime = or.checkoutTime,
                    amount = or.amount,
                    remark = or.remark,
                    userName = or.userName,
                    address = or.address
                ))
            }
            _orderList.update { current->
                orderVoList
            }
        }

    }

    fun getData(){
        viewModelScope.launch {
            val result = authRepository.getData()
            _dataList.update {result}
            var sum = 0.0
            dataList.value.forEach { or->
                sum += or.amount*0.1
            }
            orderMoney.value = sum
            orderCount.value = dataList.value.size
        }
    }
    fun getDeliveryOrder(){
        val cId = shapePreferences.getLong("cId",0)
        val op = OrdersPageQueryDTO(1,100,null,null,4,null,null,null,cId)//4->5:派送->完成
        viewModelScope.launch {
            val result = authRepository.getOrders(op)
            val orderVoList = mutableListOf<OrderVO>()
            result.forEach { or->
                orderVoList.add(OrderVO(
                    id = or.id,
                    number = or.number,
                    userId = or.userId,
                    phone = or.phone,
                    addressBookId = or.addressBookId,
                    checkoutTime = or.checkoutTime,
                    amount = or.amount,
                    remark = or.remark,
                    userName = or.userName,
                    address = or.address
                ))
            }
            _deliveryList.update { current->
                orderVoList
            }
        }

    }

    fun delivery(id:Int){

        viewModelScope.launch {
            val result = authRepository.delivery(id)
            if(result!= -1){
                getOrder()
            }
        }

    }

    fun complete(id: Int){
        viewModelScope.launch {
            val result = authRepository.complete(id)
            if(result != -1){
                getDeliveryOrder()
            }
        }
    }

    fun getOrderById(id: Int){
        viewModelScope.launch {
            val result = authRepository.getOrderById(id)
            /*if(result.orderDishes.addressBookId != null){
                result.orderDishes.address = authRepository.getAddress(
                    result.orderDishes.addressBookId!!.toInt()
                ).districtName
            }*/
            detail.value = result
        }
    }

    fun unLogin(){
        shapePreferences.edit { putString("password", null) }
    }

    fun getCompleteOrder(){
        val cId = shapePreferences.getLong("cId",0)
        val op = OrdersPageQueryDTO(1,100,null,null,5,null,null,null,cId)//4->5:派送->完成
        viewModelScope.launch {
            val result = authRepository.getOrders(op)
            val orderVoList = mutableListOf<OrderVO>()
            result.forEach { or->
                orderVoList.add(OrderVO(
                    id = or.id,
                    number = or.number,
                    userId = or.userId,
                    phone = or.phone,
                    addressBookId = or.addressBookId,
                    checkoutTime = or.checkoutTime,
                    amount = or.amount,
                    remark = or.remark,
                    userName = or.userName,
                    address = or.address
                ))
            }
            _completeList.update { current->
                orderVoList
            }
        }
    }
}

