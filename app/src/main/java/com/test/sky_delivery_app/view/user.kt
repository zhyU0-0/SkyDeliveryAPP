package com.test.sky_delivery_app.view

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.sky_delivery_app.R
import com.test.sky_delivery_app.pojo.vo.OrderVO
import com.test.sky_delivery_app.request.RetrofitClient
import com.test.sky_delivery_app.viewmodel.HttpViewModel
import com.test.sky_delivery_app.viewmodel.MapViewModel
import coil.compose.AsyncImage

@Composable
fun UserScreen(viewModel: HttpViewModel,mapViewModel: MapViewModel) {
    val context = LocalContext.current
    val employee = remember { viewModel._getEmployee() }
    val orderCount = remember { viewModel.orderCount }
    val allAmount = remember { viewModel.orderMoney }
    var ip by remember { mutableStateOf("10.0.2.2:8080") }
    val completeList by viewModel.complete.collectAsStateWithLifecycle()

    // 使用 collectAsStateWithLifecycle 来观察状态变化
    //cc
    val showCompleteList by remember { viewModel.is_show_cpList }
    val size by animateDpAsState(
        targetValue = if (viewModel.is_show_cpList.value) 500.dp else 0.dp,
        animationSpec = tween(durationMillis = 500)
    )
    val select_car by animateDpAsState(
        targetValue = if(mapViewModel.is_show_select.value) 500.dp else 0.dp,
    )
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(16.dp)

        ) {
            // 用户信息卡片
            items(
                count = 1
            ){
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 头像
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_launcher_foreground),
                                contentDescription = "用户头像",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .padding(8.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // 用户信息
                        Column(
                            modifier = Modifier
                                .width(160.dp)
                        ) {
                            Text(
                                text = employee.name ?: "未知用户",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "@${employee.username ?: "unknown"}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            ActionButton(
                                text = "退出登录",
                                icon = Icons.Default.Refresh,
                                modifier = Modifier.fillMaxSize(),
                                onClick = {
                                    viewModel.unLogin()
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                },
                            )
                        }
                    }
                }

                // 数据统计卡片
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    // 订单量卡片
                    StatCard(
                        title = "完成订单量",
                        value = orderCount.value.toString(),
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // 收入卡片
                    StatCard(
                        title = "总收入",
                        value = "¥${String.format("%.2f", allAmount.value ?: 0.0)}",
                        icon = Icons.Default.AddCircle,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }

                // 操作按钮区域
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "快捷操作",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ActionButton(
                                text = "扫码配送",
                                icon = Icons.Default.Share,
                                onClick = {
                                    val intent = Intent(context, QRActivity::class.java)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            )

                            ActionButton(
                                text = "完成订单",
                                icon = Icons.Default.Star,
                                onClick = {
                                    viewModel.getCompleteOrder()
                                    viewModel.is_show_cpList.value = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = mapViewModel.location.value.toString(),
                            onValueChange = { mapViewModel.location.value = it },
                            label = { Text("所在城市") },
                            placeholder = { Text("请输入所在城市") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "所在城市"
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ActionButton(
                                text = "更改地址",
                                icon = Icons.Default.Place,
                                onClick = {
                                    mapViewModel.updateLocation()
                                },
                                modifier = Modifier.weight(1f)
                            )

                            ActionButton(
                                text = "交通工具",
                                icon = Icons.Default.Star,
                                onClick = {
                                    mapViewModel.showSelectCar()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }


                // IP设置区域
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = ip,
                            onValueChange = { ip = it },
                            label = { Text("服务器地址") },
                            placeholder = { Text("请输入服务器IP和端口") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "服务器地址"
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ActionButton(
                                text = "更新地址",
                                icon = Icons.Default.Refresh,
                                onClick = {
                                    viewModel.shapePreferences.edit {
                                        putString("ip", ip)
                                    }
                                    RetrofitClient.init(viewModel.shapePreferences)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

        }

        // 完成订单抽屉 - 直接使用状态值
        AnimatedVisibility(
            visible = true, // 这里始终为 true，因为外层的 if 条件已经控制了显示
           /* enter = slideInVertically(
                initialOffsetY = { it }, // 从底部进入
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it }, // 向底部退出
                animationSpec = tween(durationMillis = 300)
            ),*/
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(size)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 拖拽指示器
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clickable { viewModel.is_show_cpList.value = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(4.dp)
                                .background(
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }

                    // 标题
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "完成订单列表",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "共${completeList.size}单",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    )

                    // 订单列表
                    if (completeList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "暂无完成订单",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "快去完成一些订单吧！",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            items(
                                items = completeList,
                                key = { orderVo -> orderVo.number }
                            ) { orderVo ->
                                CompleteItem(orderVo, viewModel)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    // 底部操作按钮
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                        OutlinedButton(
                            onClick =  { viewModel.is_show_cpList.value = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("关闭")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                viewModel.getData() // 刷新数据
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("刷新列表")
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(select_car)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 拖拽指示器
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clickable { mapViewModel.is_show_select.value = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(4.dp)
                                .background(
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                    }

                    // 标题
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "选择交通工具",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    )

                    // 订单列表
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        SelectButton(
                            R.drawable.ic_launcher_foreground,
                            "汽车",
                            mapViewModel.car.value,
                            0,
                            {
                                mapViewModel.car.value = 0
                                mapViewModel.updateCar()
                            }
                        )
                        Box(modifier = Modifier.width(10.dp))
                        SelectButton(
                            R.drawable.ic_launcher_foreground,
                            "摩托车",
                            mapViewModel.car.value,
                            1,
                            {
                                mapViewModel.car.value = 1
                                mapViewModel.updateCar()
                            }
                        )
                    }
                    Box(modifier = Modifier.width(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        SelectButton(
                            R.drawable.ic_launcher_foreground,
                            "自行车",
                            mapViewModel.car.value,
                            2,
                            {
                                mapViewModel.car.value = 2
                                mapViewModel.updateCar()
                            }
                        )
                        Box(modifier = Modifier.width(10.dp))
                        SelectButton(
                            R.drawable.ic_launcher_foreground,
                            "走路",
                            mapViewModel.car.value,
                            3,
                            {
                                mapViewModel.car.value = 3
                                mapViewModel.updateCar()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectButton(
    image: Int, // 如果是本地资源
    content: String,
    currentCar:Int,
    goatCar:Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .width(160.dp)
        .height(200.dp),
    imageUrl: String? = null, // 可选：支持网络图片
    imageSize: Int = 56, // 图片大小
    cornerRadius: Int = 16, // 圆角半径
) {
    var color = MaterialTheme.colorScheme.surface
    var contentColor = MaterialTheme.colorScheme.surface
    if(currentCar == goatCar){
        color = MaterialTheme.colorScheme.primary
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    }
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = contentColor
        ),
        border = BorderStroke(3.dp, color),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // 使用 Coil 加载图片
            if (imageUrl != null) {
                // 加载网络图片
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "$content 图标",
                    modifier = Modifier.size(imageSize.dp),
                    placeholder = painterResource(id = image), // 加载时的占位图
                    error = painterResource(id = image) // 错误时的备用图
                )
            } else {
                // 加载本地资源图片
                AsyncImage(
                    model = image, // 本地资源 ID
                    contentDescription = "$content 图标",
                    modifier = Modifier.size(imageSize.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = contentColor,
                maxLines = 2
            )
        }
    }
}

@Composable
fun CompleteItem(order: OrderVO, viewModel: HttpViewModel) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("OrderId", order.id.toInt())
                Log.v("OrderId", order.id.toString())
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 订单ID - 左侧圆形标识
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = order.id.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 订单信息 - 中间区域
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "订单号: ${order.number}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                order.address?.let { address ->
                    Text(
                        text = "地址: $address",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                order.phone?.let { phone ->
                    Text(
                        text = "电话: $phone",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

}

// StatCard 和 ActionButton 保持不变...
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}