package com.shingekinokyojin.wallrose.ui.profile

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController
){
    Text(text = "profile")
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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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
                .background(color = MaterialTheme.colorScheme.secondary)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.secondary)
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
    modifier: Modifier = Modifier
){
    WallRoseTheme {
        Column {

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