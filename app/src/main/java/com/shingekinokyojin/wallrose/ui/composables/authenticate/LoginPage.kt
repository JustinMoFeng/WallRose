package com.shingekinokyojin.wallrose.ui.composables.authenticate

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation.Companion.None
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDetailAppBar
import com.shingekinokyojin.wallrose.ui.screens.AuthenticateViewModel
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme
import java.net.PasswordAuthentication

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
                .background(MaterialTheme.colorScheme.primary),
                horizontalAlignment = Alignment.CenterHorizontally
            ){


                Spacer(modifier = Modifier.weight(0.6f))


                Image(painter = painterResource(id = R.drawable.app_logo), contentDescription = "app_logo")


                Spacer(modifier = Modifier.weight(0.2f))



                LoginBody(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it),
                    authenticateViewModel = authenticateViewModel,
                    onGoToRegister = {
                        navController.navigate("register")
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
    onGoToRegister: () -> Unit = {}
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
                                onClick = { showDialog = false }
                            ) {
                                Text("确定", color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
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
                    .background(MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.medium),
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

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun PreviewLoginBody() {
    LoginBody(
        authenticateViewModel = AuthenticateViewModel(),
    )
}

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

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun PreviewLoginPage() {
    LoginPage(
        authenticateViewModel = AuthenticateViewModel(),
        navController = NavController(LocalContext.current)
    )
}