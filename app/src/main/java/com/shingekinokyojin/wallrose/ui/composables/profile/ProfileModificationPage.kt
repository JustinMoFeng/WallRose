package com.shingekinokyojin.wallrose.ui.composables.profile

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDetailAppBar
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileModificationPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    title: String,
    labels: List<String>,
    contents: List<String>,
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
                    title = title,
                    onLeftClick = {
                        navController.popBackStack()
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileModificationBody(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(it),
                    labels = labels,
                    contents = contents,
                )
            }
        }
    }
}

@Composable
fun ProfileModificationBody(
    modifier: Modifier = Modifier,
    labels: List<String>,
    contents: List<String>,
) {
    WallRoseTheme {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.secondary),
        ) {
            labels.forEachIndexed { index, label ->
                ProfileModificationItem(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = label,
                    content = contents[index],
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                onClick = {  },
                content = {
                    Text(
                        text = "确认",
                        modifier = Modifier
                            .wrapContentWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            )
        }
    }
}

@Composable
fun ProfileModificationItem(
    modifier: Modifier = Modifier,
    label: String,
    content: String,
) {
    WallRoseTheme {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier
                    .wrapContentWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            BasicTextField(
                value = content,
                onValueChange = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .padding(10.dp),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.tertiary),
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.tertiary),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        innerTextField()
                    }
                }
            )
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ProfileModificationPageDarkPreview() {
    WallRoseTheme {
        ProfileModificationPage(navController = NavController(LocalContext.current), title = "修改昵称", labels = listOf("昵称"), contents = listOf("mofengyichen"))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ProfileModificationBodyDarkPreview() {
    WallRoseTheme {
        ProfileModificationBody(labels = listOf("昵称"), contents = listOf("mofengyichen"))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ProfileModificationItemDarkPreview() {
    WallRoseTheme {
        ProfileModificationItem(label = "昵称", content = "mofengyichen")
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",showBackground = true, backgroundColor = 0xFF300000)
@Composable
fun ProfileModificationItemDarkPreview2() {
    WallRoseTheme {
        ProfileModificationPage(navController = NavController(LocalContext.current), title = "修改昵称", labels = listOf("新密码", "确认密码"), contents = listOf("mofengyichen", "mofengyichen"))
    }
}

