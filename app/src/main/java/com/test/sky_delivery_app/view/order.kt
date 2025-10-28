package com.test.sky_delivery_app.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.sky_delivery_app.myKey.YourKey
import com.test.sky_delivery_app.pojo.vo.OrderVO
import com.test.sky_delivery_app.viewmodel.HttpViewModel
import com.test.sky_delivery_app.websocket.OkHttpController

@Composable
fun OrderScreen(viewModel: HttpViewModel){
    val orderList by viewModel.orderList.collectAsStateWithLifecycle()
    val listStatus = rememberLazyListState()
    var dlv_success by remember {viewModel.delivery_succeeful}
    val context = LocalContext.current
    Box{
        Column {
            Text("Order")
            Button(
                onClick = {
                    viewModel.getOrder()
                }
            ) {Text(
                text = "orderList",
            )}
            LazyColumn(
                state = listStatus,
                modifier = Modifier
                    .width(200.dp)
            ) {

                items(
                    items = orderList,
                    key = { orderVo -> orderVo.number }
                ){orderVo ->
                    OrderItem(orderVo,viewModel)
                }
            }
        }
        if(dlv_success){
            Card (
                modifier = Modifier
                    .width(200.dp)
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ){
                Column {
                    Text(text = "抢单成功", fontSize = 30.sp)
                    Box(modifier = Modifier.height(30.dp))
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Button(
                            modifier = Modifier
                                .background(Color.Green),
                            onClick = {
                                viewModel.delivery_succeeful.value = false
                            }
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun Greeting(
    name: String, modifier:
    Modifier = Modifier,okHttpController: OkHttpController,
    viewModel: HttpViewModel
) {

    val deliveryList by viewModel.deliveryList.collectAsStateWithLifecycle()

    var str by remember { mutableStateOf("aa") }
    val listStatus = rememberLazyListState()
    var dlv_success by remember {viewModel.delivery_succeeful}
    val cpl_success by remember { viewModel.delivery_succeeful }
    /*var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("123456") }*/
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            /*TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("username") },
                placeholder = { Text("username") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("password") },
                placeholder = { Text("password") },
                modifier = Modifier.fillMaxWidth()
            )*/
            Text(
                text = "Hello $str!",
                modifier = modifier
            )
            Row {
                /*Button(
                    onClick = {
                        viewModel.login(
                            username,
                            password,
                            {body-> str = body.toString()}
                        )
                    }
                ) {Text(
                    text = "login",
                    modifier = modifier
                )}*/
                Button(
                    onClick = {
                        okHttpController.connectWS()
                    }
                ) {Text(
                    text = "ws",
                    modifier = modifier
                )}


            }
            Row {


            }

        }


    }



}

@Composable
fun OrderItem(order: OrderVO,wsViewModel: HttpViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp),
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
                        wsViewModel.delivery(order.id.toInt())
                    }
                ) { }
            }
        }

    }
}


