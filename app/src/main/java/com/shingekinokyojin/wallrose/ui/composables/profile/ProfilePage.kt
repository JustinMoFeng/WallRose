package com.shingekinokyojin.wallrose.ui.composables.profile

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseAppBar
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDrawer
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController
){

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        modifier = modifier,
        topBar = {
            WallRoseAppBar(
                title = stringResource(id = R.string.tabbar_profile),
                onLeftClick = {
                    if(drawerState.isOpen) scope.launch { drawerState.close() }
                    else scope.launch { drawerState.open() }
                }
            )
        }
    )
    {
        ModalNavigationDrawer(
            drawerContent = {
                WallRoseDrawer(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxWidth(0.648f)
                        .fillMaxHeight()
                        .padding(it)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),

            drawerState = drawerState,
        ) {
            ProfileContent(
                navController = navController,
                modifier = Modifier
                    .padding(it),
            )
        }

    }
}


@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    navController: NavController
){
    WallRoseTheme {
        Column(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.secondary)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ProfileUserPart(modifier = Modifier.weight(1f))

            Spacer(
                modifier = Modifier.weight(0.05f)
            )

            ProfileUserChatHistory(
                contenttList = listOf(
                    "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
                    "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
                    "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
                    "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
                    "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
                    "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
                    "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
                    "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？"
                ),
                modifier = Modifier
                    .weight(5f)
                    .padding(horizontal = 20.dp)
            )

            Spacer(
                modifier = Modifier.weight(0.05f)
            )

            Button(
                onClick = { navController.navigate(RouteConfig.ROUTE_CHAT) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .height(55.dp)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.profile_new_chat),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

}


@Composable
fun ProfileUserPart(
    modifier: Modifier = Modifier,
    userName: String = "小仙女",
){
    WallRoseTheme {
        Row(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.secondary)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.weight(0.2f))

            Image(
                painter = painterResource(id = R.drawable.profile_user_example),
                contentDescription = "样例用户头像",
                modifier = Modifier
                    .clip(CircleShape)
                    .weight(1.8f)
            )

            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .weight(4f)
                    .padding(start = 20.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.profile_setting),
                contentDescription = "设置",
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .weight(1f)
            )
        }
    }
}

@Composable
fun ProfileUserChatHistoryItem(
    modifier: Modifier = Modifier,
    chatHistoryContent: String = "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
){
    WallRoseTheme {
        Column(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.primary)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(id = R.drawable.profile_chat_history),
                    contentDescription = "聊天记录",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .weight(1f)
                )

                Spacer(modifier = Modifier.weight(0.2f))

                // text 溢出自动隐藏
                Text(
                    text = chatHistoryContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(8f)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .padding(horizontal = 5.dp)
                .background(color = MaterialTheme.colorScheme.tertiary)
            )
        }

    }
}

@Composable
fun ProfileUserChatHistory(
    modifier: Modifier = Modifier,
    contenttList: List<String>
){
    WallRoseTheme {
        Column(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(10.dp)
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.profile_chat_history),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                    )

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .padding(horizontal = 5.dp)
                            .background(color = MaterialTheme.colorScheme.tertiary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            LazyColumn(
                modifier = Modifier
            ) {
                items(contenttList){ content ->
                    Spacer(modifier = Modifier.height(5.dp))
                    ProfileUserChatHistoryItem(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        chatHistoryContent = content
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

        }
    }
}





@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewProfileUserPart(){
    ProfileUserPart()
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewProfileUserChatHistoryItem(){
    ProfileUserChatHistoryItem()
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewProfileUserChatHistory(){
    ProfileUserChatHistory(
        contenttList = listOf(
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？",
            "解析植物奶：为什么突然流行？有哪些种类？解析植物奶：为什么突然流行？有哪些种类？"
        )
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewProfileContent(){
    ProfileContent(
        navController = NavController(LocalContext.current)
    )
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewProfilePage(){
    ProfilePage(
        navController = NavController(LocalContext.current),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
}