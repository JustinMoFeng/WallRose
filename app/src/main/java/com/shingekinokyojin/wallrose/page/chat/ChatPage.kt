package com.shingekinokyojin.wallrose.page.chat

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme


@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    navController: NavController
){
    Text(text = "chatPage")
    Button(onClick = { navController.navigate(RouteConfig.ROUTE_PROFILE) }) {
        Text(text = "goProfile")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    modifier: Modifier
){

    var text by remember { mutableStateOf("") }

    WallRoseTheme {
        Row(modifier = modifier) {
            TextField(
                value = text ,
                onValueChange = {text = it},
                shape = RectangleShape,
                label = {
                    Text(text = "请输入内容开始聊天")
                }
            )
        }
    }
}