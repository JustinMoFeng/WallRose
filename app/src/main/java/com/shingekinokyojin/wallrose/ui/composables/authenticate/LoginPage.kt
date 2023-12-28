package com.shingekinokyojin.wallrose.ui.composables.authenticate

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDetailAppBar
import com.shingekinokyojin.wallrose.ui.screens.AuthenticateViewModel
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authenticateViewModel: AuthenticateViewModel
) {
    WallRoseTheme {
        Scaffold(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primary),
            topBar = {
                WallRoseDetailAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    title = "登录",
                    onLeftClick = {
                        navController.popBackStack()
                    }
                )
            }
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
                .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ){


                Spacer(modifier = Modifier.weight(0.6f))


                Image(painter = painterResource(id = R.drawable.app_logo), contentDescription = "app_logo")


                Spacer(modifier = Modifier.weight(0.2f))



                LoginBody(
                    modifier = Modifier
                        .fillMaxWidth(),
                    authenticateViewModel = authenticateViewModel,
                    onGoToRegister = {
                        navController.navigate(RouteConfig.ROUTE_REGISTER)
                    },
                    onGoToChat = {
                        navController.navigate(RouteConfig.ROUTE_CHAT)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
            }


        }
    }

}


@Composable
fun LoginBody(
    modifier: Modifier = Modifier,
    authenticateViewModel: AuthenticateViewModel,
    onGoToRegister: () -> Unit = {},
    onGoToChat: () -> Unit = {}
){
    WallRoseTheme{
        var showDialog by remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextInput(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight(),
                onValueChange = { authenticateViewModel.username = it },
                imageValue = R.drawable.authenticate_username,
                placeHolderValue = "请输入用户名",
                username = authenticateViewModel.username
            )

            Spacer(modifier = Modifier.height(50.dp))

            PasswordTextInput(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight(),
                onValueChange = { authenticateViewModel.password = it },
                imageValue = R.drawable.authenticate_password,
                placeHolderValue = "请输入密码",
                password = authenticateViewModel.password
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "忘记密码",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable {
                        showDialog = true
                    }
                )

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("提示") },
                        text = { Text("找回密码功能暂未开通") },
                        confirmButton = {
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Text("确定", color = MaterialTheme.colorScheme.tertiary)
                            }
                        },
                        containerColor = Color(0xFF111111),
                        textContentColor = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSecondary,
                            shape = RoundedCornerShape(10.dp)
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "注册账号",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable {
                        onGoToRegister()
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium)
                    .clickable {
                        if(authenticateViewModel.username == "" || authenticateViewModel.password == ""){
                            authenticateViewModel.loginState = "用户名或密码不能为空"
                            return@clickable
                        }
                        authenticateViewModel.login()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "登录",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }


            if(authenticateViewModel.loginState != ""&&authenticateViewModel.loginState!= "true"){
                AlertDialog(
                    onDismissRequest = { authenticateViewModel.loginState = "" },
                    title = { Text("提示") },
                    text = { Text(authenticateViewModel.loginState) },
                    confirmButton = {
                        Button(
                            onClick = { authenticateViewModel.loginState = "" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("确定", color = MaterialTheme.colorScheme.tertiary)
                        }
                    },
                    containerColor = Color(0xFF111111),
                    textContentColor = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(10.dp)
                    )
                )
            }else if(authenticateViewModel.loginState == "true"){
                authenticateViewModel.loginState = ""
                onGoToChat()
            }

        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun PreviewTextInput() {
    TextInput(
        onValueChange = { },
        imageValue = R.drawable.authenticate_username,
        placeHolderValue = "请输入用户名",
        username = ""
    )
}

//@Preview
//@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
//@Composable
//fun PreviewLoginBody() {
//    LoginBody(
//        authenticateViewModel = AuthenticateViewModel(),
//    )
//}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun PreviewPasswordTextInput() {
    PasswordTextInput(
        onValueChange = { },
        imageValue = R.drawable.authenticate_password,
        placeHolderValue = "请输入密码",
        password = ""
    )
}

//@Preview
//@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
//@Composable
//fun PreviewLoginPage() {
//    LoginPage(
//        authenticateViewModel = AuthenticateViewModel(),
//        navController = NavController(LocalContext.current)
//    )
//}