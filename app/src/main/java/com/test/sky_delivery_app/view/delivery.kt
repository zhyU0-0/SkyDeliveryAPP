package com.test.sky_delivery_app.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.sky_delivery_app.myKey.YourKey
import com.test.sky_delivery_app.pojo.vo.OrderVO
import com.test.sky_delivery_app.viewmodel.HttpViewModel

@Composable
fun DeliveryScreen(viewModel: HttpViewModel){
    val deliveryList by viewModel.deliveryList.collectAsStateWithLifecycle()

    var str by remember { mutableStateOf("aa") }
    val listStatus = rememberLazyListState()
    val cpl_success by remember { viewModel.delivery_succeeful }
    /*var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("123456") }*/
    val context = LocalContext.current
    Box{
        Column {
            Row {
                Button(
                    onClick = {
                        val intent = Intent(context, MapActivity::class.java)
                        context.startActivity(intent)
                    }
                ) { Text("地图") }
            }
            Text("Delivery")
            Button(
                onClick = {
                    viewModel.getDeliveryOrder()
                }
            ) {Text(
                text = "deliveryList",
            )}
        }
        LazyColumn(
            state = listStatus,
            modifier = Modifier
                .width(200.dp)
        ) {

            items(
                items = deliveryList,
                key = { orderVo -> orderVo.number }
            ){orderVo ->
                DeliveryItem(orderVo,viewModel)
            }
        }
    }

}
@Composable
fun DeliveryItem(order: OrderVO,wsViewModel: HttpViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
            .background(Color.Blue),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row {
            Text(text = order.id.toString(), style = MaterialTheme.typography.titleMedium,fontSize = 50.sp)
            /*Column(modifier = Modifier.padding(16.dp)) {
                Text(text = order.number, style = MaterialTheme.typography.titleMedium)
                Text(text = order.address, color = Color.Gray)
                Text(text = order.phone.toString(), color = Color.Black, fontSize = 30.sp)
            }*/
            Column {
                Button(
                    onClick = {
                        wsViewModel.complete(order.id.toInt())
                    }
                ) { YourKey().key }
            }
        }

    }
}