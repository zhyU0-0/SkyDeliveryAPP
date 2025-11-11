package com.test.sky_delivery_app.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amap.api.maps.MapsInitializer
import com.test.sky_delivery_app.pojo.OrderDetail
import com.test.sky_delivery_app.pojo.Orders
import com.test.sky_delivery_app.view.ui.theme.SkyDeliveryAppTheme
import com.test.sky_delivery_app.viewmodel.HttpViewModel
import com.test.sky_delivery_app.viewmodel.MapViewModel

class DetailActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var wsViewModel: HttpViewModel
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = this.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        wsViewModel = HttpViewModel(this,sharedPreferences)
        mapViewModel = MapViewModel(this,sharedPreferences)
        val orderId = intent.getIntExtra("OrderId",-1)
        Log.v("OrderId",orderId.toString())

        wsViewModel.getOrderById(orderId.toString().toInt())
        loadMap()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkyDeliveryAppTheme {
                DetailScreen({finish()},wsViewModel,mapViewModel)
            }
        }
    }

    fun loadMap(){
        try {
            // 设置是否在 stop 的时候杀死地图进程
            MapsInitializer.updatePrivacyShow(this, true, true)
            MapsInitializer.updatePrivacyAgree(this, true)

            /*// 初始化导航（重要）
            com.amap.api.navi.AMapNavi.setAppOffline(this, 9) // 9代表离线数据版本
            com.amap.api.navi.AMapNavi.updatePrivacyShow(this, true, true)
            com.amap.api.navi.AMapNavi.updatePrivacyAgree(this, true)*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBackClick:()->Unit,
    viewModel: HttpViewModel,
    mapViewModel: MapViewModel
){

    val detail by remember { viewModel.detail }
    val order = detail.orderDishes
    val orderDetails = detail.orderDetailList
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. 订单基本信息卡片
            OrderBasicInfoCard(order)

            Spacer(modifier = Modifier.height(8.dp))

            // 2. 配送信息卡片
            DeliveryInfoCard(order,mapViewModel)

            Spacer(modifier = Modifier.height(8.dp))

            // 3. 商品列表卡片
            OrderItemsCard(orderDetails, order.amount)

            Spacer(modifier = Modifier.height(8.dp))

            // 4. 订单金额详情卡片
            OrderAmountCard(order)
        }
    }
}

@Composable
private fun OrderBasicInfoCard(order: Orders) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "订单信息",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoRow("订单编号", order.number.toString())
            InfoRow("订单状态", OrderStatusHelper.getStatusText(order.status))
            InfoRow("支付方式", OrderStatusHelper.getPayMethodText(order.payMethod))
            InfoRow("支付状态", OrderStatusHelper.getPayStatusText(order.payStatus))
            InfoRow("下单时间", order.orderTime)
            InfoRow("结账时间", order.checkoutTime)

            order.remark?.takeIf { it.isNotBlank() }?.let { remark ->
                InfoRow("订单备注", remark)
            }
        }
    }
}

@Composable
private fun DeliveryInfoCard(order: Orders,mapViewModel: MapViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "配送信息",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoRow("收货人", order.consignee)
            InfoRow("联系电话", order.phone)
            Card (onClick = {
                mapViewModel.goat.value = order.address.toString()
                mapViewModel.search()
            }){
                InfoRow("配送地址", order.address)
            }

            InfoRow("预计送达", order.estimatedDeliveryTime)

            order.deliveryTime?.let { deliveryTime ->
                InfoRow("实际送达", deliveryTime)
            }

            InfoRow("配送状态", OrderStatusHelper.getDeliveryStatusText(order.deliveryStatus))

            order.cancelReason?.takeIf { it.isNotBlank() }?.let { reason ->
                InfoRow("取消原因", reason)
            }

            order.rejectionReason?.takeIf { it.isNotBlank() }?.let { reason ->
                InfoRow("拒绝原因", reason)
            }
        }
    }
}

@Composable
private fun OrderItemsCard(orderDetails: List<OrderDetail>, totalAmount: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "商品清单",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            orderDetails.forEach { item ->
                OrderItemRow(item)
                if (item != orderDetails.last()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.name,
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium
            )

            if (!item.dishFlavor.isNullOrBlank()) {
                Text(
                    text = "口味: ${item.dishFlavor}",
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "¥${item.amount}",
                fontSize = 20.sp,
                color = Color.Red
            )
        }

        Text(
            text = "×${item.number}",
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OrderAmountCard(order: Orders) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "费用明细",

                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            AmountRow("商品总额", "¥${order.amount}")
            AmountRow("打包费", "¥${order.packAmount}")
            AmountRow("餐具数量", order.tablewareNumber.toString())

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "实付金额",

                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Text(
                    text = "¥${order.amount + (order.packAmount?:0)}",

                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            color = Color.Gray
        )
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AmountRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 20.sp,)
        Text(text = value, fontSize = 20.sp,)
    }
}



// 状态转换工具
object OrderStatusHelper {

    fun getStatusText(status: Int?): String {
        return when (status) {
            1 -> "待付款"
            2 -> "待接单"
            3 -> "已接单"
            4 -> "派送中"
            5 -> "已完成"
            6 -> "已取消"
            else -> "未知状态"
        }
    }

    fun getPayMethodText(method: Int?): String {
        return when (method) {
            1 -> "微信支付"
            2 -> "支付宝"
            else -> "其他支付"
        }
    }

    fun getPayStatusText(status: Int?): String {
        return when (status) {
            1 -> "已支付"
            0 -> "未支付"
            else -> "支付异常"
        }
    }

    fun getDeliveryStatusText(status: Int?): String {
        return when (status) {
            0 -> "未派送"
            1 -> "派送中"
            2 -> "已送达"
            else -> "未知状态"
        }
    }
}