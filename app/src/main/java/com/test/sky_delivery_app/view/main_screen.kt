package com.test.sky_delivery_app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.test.sky_delivery_app.view.DeliveryScreen
import com.test.sky_delivery_app.view.OrderScreen
import com.test.sky_delivery_app.view.UserScreen
import com.test.sky_delivery_app.viewmodel.HttpViewModel
import com.test.sky_delivery_app.viewmodel.MapViewModel


@Composable
fun MainScreen(
    viewModel: HttpViewModel,
    mapViewModel: MapViewModel
) {
    val messageList by viewModel.messageList.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    var showExitDialog by remember { mutableStateOf(false) }
    val overlapOffset = 20 //叠加偏移量

    BackHandler {
        showExitDialog = true
    }
    Box{
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController,viewModel)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "orders",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("orders") { OrderScreen(viewModel) }
                composable("delivery") { DeliveryScreen(viewModel,mapViewModel) }
                composable("user") { UserScreen(viewModel,mapViewModel) }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            messageList.forEachIndexed { index, mag ->
                val offset = index * overlapOffset

                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .height(220.dp)
                        .offset(x = offset.dp, y = offset.dp)
                        .zIndex((messageList.size - index).toFloat()) // 确保前面的卡片在上面
                        .clickable { viewModel.confine(mag.orderId) },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp + (index * 2).dp // 越靠前的卡片阴影越深
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f - (index * 0.1f)) // 叠加透明度变化
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF667eea).copy(alpha = 0.1f),
                                        Color(0xFF764ba2).copy(alpha = 0.05f)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // 标题区域
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "催单提醒",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF764ba2)
                                )

                                // 催单计数标签
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFFF6B6B),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}/${messageList.size}",
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 订单信息
                            Text(
                                text = "订单号: ${mag.orderId}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D3748)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "客户正在等待，请尽快处理！",
                                fontSize = 14.sp,
                                color = Color(0xFF718096),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 时间戳（如果有的话）
                            Text(
                                text = "刚刚",
                                fontSize = 12.sp,
                                color = Color(0xFFA0AEC0)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 操作按钮
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { viewModel.confine(mag.orderId) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF48BB78)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 2.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "确认",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "确认处理",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // 紧急标识
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "紧急",
                                tint = Color(0xFFFF6B6B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun BottomNavigationBar(navController: NavHostController,viewModel: HttpViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItems = listOf(
        BottomNavItem(
            title = "订单",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "orders"
        ),
        BottomNavItem(
            title = "派送",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "delivery"
        ),
        BottomNavItem(
            title = "我的",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "user"
        )
    )

    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 避免重复点击时创建多个实例
                        launchSingleTop = true
                        // 恢复状态
                        restoreState = true
                    }
                    if(item.title == "订单"){viewModel.getOrder()}
                    if(item.title == "派送"){viewModel.getDeliveryOrder()}
                    if(item.title == "我的"){viewModel.getData()}
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) {
                            item.selectedIcon
                        } else {
                            item.unselectedIcon
                        },
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) }
            )
        }
    }
}
data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)
