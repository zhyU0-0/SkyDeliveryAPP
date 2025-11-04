package com.test.sky_delivery_app

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.test.sky_delivery_app.pojo.vo.OrderVO
import com.test.sky_delivery_app.view.DeliveryScreen
import com.test.sky_delivery_app.view.MapActivity
import com.test.sky_delivery_app.view.OrderScreen
import com.test.sky_delivery_app.view.UserScreen
import com.test.sky_delivery_app.viewmodel.HttpViewModel
import kotlin.toString


@Composable
fun MainScreen(
    viewModel: HttpViewModel,

) {
    val messageList by viewModel.messageList.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    var showExitDialog by remember { mutableStateOf(false) }
    BackHandler {
        showExitDialog = true
    }
    Box{
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "orders",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("orders") { OrderScreen(viewModel) }
                composable("delivery") { DeliveryScreen(viewModel) }
                composable("user") { UserScreen(viewModel) }
            }
        }
        messageList.forEach {mag->
            Card (
                modifier = Modifier
                    .width(200.dp)
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ){
                Column {
                    Text(text = mag.orderId.toString()+"催单", fontSize = 30.sp)
                    Box(modifier = Modifier.height(30.dp))
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Button(
                            modifier = Modifier
                                .background(Color.Green),
                            onClick = {
                                viewModel.confine(mag.orderId)
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
fun BottomNavigationBar(navController: NavHostController) {
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
