package com.test.sky_delivery_app.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.test.sky_delivery_app.pojo.MassageDTO
import com.test.sky_delivery_app.pojo.OrdersPageQueryDTO
import com.test.sky_delivery_app.pojo.vo.OrderVO
import com.test.sky_delivery_app.websocket.OkHttpController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.find
import kotlin.collections.forEach
import kotlin.collections.minus
import kotlin.collections.plus
import kotlin.math.log

class HttpViewModel(context: Context, val shapePreferences: SharedPreferences) : ViewModel() {

    // 使用下划线前缀表示可变的内部状态
    private val _messageList = MutableStateFlow<List<MassageDTO>>(emptyList())
    private val _orderList = MutableStateFlow<List<OrderVO>>(emptyList())
    private val _deliveryList = MutableStateFlow<List<OrderVO>>(emptyList())
    // 公开的不可变状态
    val messageList: StateFlow<List<MassageDTO>> = _messageList.asStateFlow()
    val orderList: StateFlow<List<OrderVO>> = _orderList.asStateFlow()
    val deliveryList: StateFlow<List<OrderVO>> = _deliveryList.asStateFlow()
    val delivery_succeeful = mutableStateOf(false)
    val complete_succeeful = mutableStateOf(false)

    val okHttpController =  OkHttpController(context,shapePreferences,{
        massageDTO ->
        if(shapePreferences.getLong("cId",0)==massageDTO.employeeId){
            _messageList.update { currentList ->
                currentList + massageDTO
            }
        }
        Log.v("The Message", massageDTO.toString())
        Log.v("MessageList",_messageList.toString())
    })


    fun login(userName: String, password: String, callback: (Result<String>) -> Unit): Call {
        //登录后再次验证身份，方便获取employeeId
        val call = okHttpController.login(
            userName,
            password,
            {
                callback
                getOrder()//起身份验证作用
                getDeliveryOrder()
            }
        )


        return call
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
        okHttpController.connectWS()
    }

    fun send(){
        okHttpController.okHttpWebSocketService.sendMessage("")
    }

    fun destroy(){
        okHttpController.close()
    }
    
    fun getOrder(){

        val op = OrdersPageQueryDTO(1,100,null,null,3,null,null,null,null)//3->4:店家接单->派送中
        okHttpController.getOrders(op, {orderRecords ->
            val orderVoList = mutableListOf<OrderVO>()
            orderRecords.forEach { or->
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
        })

    }
    fun getDeliveryOrder(){
        val cId = shapePreferences.getLong("cId",0)
        val op = OrdersPageQueryDTO(1,100,null,null,4,null,null,null,cId)//4->5:派送->完成
        okHttpController.getOrders(op, {orderRecords ->
            val orderVoList = mutableListOf<OrderVO>()
            orderRecords.forEach { or->
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
        })

    }

    fun delivery(id:Int){
        okHttpController.delivery(id,{str->
            if(str=="200"){
                delivery_succeeful.value = true
            }
            getOrder()
        })
    }

    fun complete(id: Int){
        okHttpController.complete(id,{str->
            if(str=="200"){
                complete_succeeful.value = true
            }
            getDeliveryOrder()
        })
    }

}

