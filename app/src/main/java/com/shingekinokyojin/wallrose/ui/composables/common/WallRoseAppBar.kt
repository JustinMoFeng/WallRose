package com.shingekinokyojin.wallrose.ui.composables.common

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.services.FloatingWindowService
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme

@Composable
fun WallRoseAppBar(
    modifier: Modifier = Modifier,
    title: String = "WallRose",
    onLeftClick: () -> Unit = {}
){
    val context = LocalContext.current
    WallRoseTheme {
        Row(
            modifier = modifier
                .fillMaxWidth() // 使 Row 填满父容器的宽度
                .background(color = MaterialTheme.colorScheme.primary), // 添加一些内边距
            verticalAlignment = Alignment.CenterVertically // 垂直居中子元素
        ) {
            // 点击事件绑定到 Image 上
            IconButton(onClick = { onLeftClick() }) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.appbar_menu),
                    contentDescription = "Menu"
                )
            }

            // 使用 Spacer 来推动 Text 到中间
            Spacer(modifier = Modifier.weight(1f))

            // Text 位于 Row 的中间
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            // 使用 Spacer 来平衡右侧空间
            Spacer(modifier = Modifier.weight(1f))

            // 开启悬浮窗按钮
            IconButton(onClick = {

            }) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.appbar_floating),
                    contentDescription = "Floating"
                )
            }
        }
    }

}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewWallRoseChatAppBar(){
    WallRoseAppBar()
}