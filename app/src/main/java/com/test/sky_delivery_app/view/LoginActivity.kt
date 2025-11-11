package com.test.sky_delivery_app.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.test.sky_delivery_app.MainActivity
import com.test.sky_delivery_app.request.RetrofitClient
import com.test.sky_delivery_app.view.ui.theme.SkyDeliveryAppTheme
import com.test.sky_delivery_app.viewmodel.HttpViewModel


class LoginActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var wsViewModel: HttpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences("AppData", Context.MODE_PRIVATE)
        RetrofitClient.init(
            sharedPreferences
        )
        wsViewModel = HttpViewModel(this,sharedPreferences)
        wsViewModel.is_auth(
            {
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
                Log.v("Login","successful")
            },
            {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            },{
                finish()
            }
        )
        setContent {
            SkyDeliveryAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        wsViewModel,
                        {
                            val intent = Intent(this, MainActivity::class.java)
                                .apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK//清空栈
                                }
                            this.startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        wsViewModel.destroy()
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: HttpViewModel,
    goto: () -> Unit
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("123456") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 标题
        Text(
            text = "欢迎登录",
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // 用户名输入框
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            placeholder = { Text("请输入用户名") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户名"
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 密码输入框
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            placeholder = { Text("请输入密码") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "密码"
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // 登录按钮
        Button(
            onClick = {
                goto()
                focusManager.clearFocus()
                viewModel.login(
                    username,
                    password,
                    {
                        Log.v("goto","goto")
                        /*goto()*/
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(56.dp)
        ) {
            Text(
                text = "登录",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 忘记密码链接
        TextButton(
            onClick = {
                // 处理忘记密码逻辑
                Toast.makeText(context, "忘记密码功能", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("忘记密码?")
        }
    }
}
