package com.shingekinokyojin.wallrose.ui.composables.chat

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.data.DefaultAppContainer
import com.shingekinokyojin.wallrose.model.AssistantMessage
import com.shingekinokyojin.wallrose.model.Chat
import com.shingekinokyojin.wallrose.model.Message
import com.shingekinokyojin.wallrose.model.UserMessage
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDetailAppBar
import com.shingekinokyojin.wallrose.ui.screens.ChatViewModel
import com.shingekinokyojin.wallrose.ui.screens.UserViewModel
import com.shingekinokyojin.wallrose.ui.theme.WallRoseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    chatViewModel: ChatViewModel
){
    WallRoseTheme {
        Scaffold(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primary),
            topBar = {
                WallRoseDetailAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = "聊天历史",
                    onLeftClick = {
                        navController.popBackStack()
                    }
                )
            }
        ) {
            ChatHistoryBody(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                chatHistory = userViewModel.chosenChatHistory,
                userViewModel= userViewModel,
                onGoChat = {
                    chatViewModel.currentChatId = userViewModel.chosenChatHistory?._id.toString()
                    // 获取最后一条message
                    val lastMessage = userViewModel.chosenChatHistory?.messages?.last()
                    chatViewModel.currentMessage = if(lastMessage is AssistantMessage) lastMessage.content.toString() else ""
                    navController.navigate("chat")
                }
            )
            
            
        }
    }

}

@Composable
fun ChatHistoryBody(
    modifier: Modifier = Modifier,
    chatHistory: Chat?,
    userViewModel: UserViewModel,
    onGoChat: () -> Unit
) {
    WallRoseTheme{
        if (chatHistory != null) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(MaterialTheme.colorScheme.primary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (message in chatHistory.messages) {
                    when (message) {
                        is UserMessage -> {
                            ChatHistoryItem(
                                message = message as UserMessage,
                                type = 0,
                                userViewModel = userViewModel
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        is AssistantMessage -> {
                            ChatHistoryItem(
                                message = message as AssistantMessage,
                                type = 1,
                                userViewModel = userViewModel
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        else -> {

                        }
                    }

                }
                
                Button(
                    onClick = {
                        onGoChat()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text(text = "继续本聊天")
                }
                
            }

        }
    }

}

@Composable
fun ChatHistoryItem(
    modifier: Modifier = Modifier,
    message: Message,
    type: Int,
    userViewModel: UserViewModel
) {
    WallRoseTheme {
        if (type == 0) {
            val t = message as UserMessage
            ChatMyMessage(
                modifier = modifier,
                myAvatarUrl = DefaultAppContainer.url + userViewModel.myAvatarUrl,
                myMessage = t.content,
                myUserName = userViewModel.myUsername
            )
        } else if (type == 1) {
            val t = message as AssistantMessage
            if(message.content != null && message.content != ""){
                ChatGPTMessage(
                    modifier = modifier,
                    message = t
                )
            }

        }
    }


}

@Composable
fun ChatGPTMessage(
    modifier: Modifier = Modifier,
    message: AssistantMessage
) {
    WallRoseTheme{
        val t = message.content
        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(R.drawable.gpt_head),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 5.dp, top = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .padding(start = 10.dp, end = 10.dp),
            ) {

                Text(
                    text = "牧濑红莉牺",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 5.dp),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    ),
                )

                if (t != null) {
                    Text(
                        text = t,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        textAlign = TextAlign.Start,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                    )
                }
            }
        }
    }

}

