package com.shingekinokyojin.wallrose.ui.composables.chat

import android.app.Application
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.shingekinokyojin.wallrose.data.AppContainer
import com.shingekinokyojin.wallrose.data.DefaultAppContainer
import com.shingekinokyojin.wallrose.model.AssistantMessage
import com.shingekinokyojin.wallrose.model.Chat
import com.shingekinokyojin.wallrose.model.Message
import com.shingekinokyojin.wallrose.model.SystemMessage
import com.shingekinokyojin.wallrose.model.ToolMessage
import com.shingekinokyojin.wallrose.model.UserMessage
import com.shingekinokyojin.wallrose.ui.composables.common.WallRoseDetailAppBar
import com.shingekinokyojin.wallrose.ui.screens.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel
){
    Scaffold(
        modifier = modifier,
        topBar = {
            WallRoseDetailAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = "聊天历史",
            )
        }
    ) {
        ChatHistoryBody(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it),
            chatHistory = userViewModel.chosenChatHistory,
            userViewModel= userViewModel
        )
    }
}

@Composable
fun ChatHistoryBody(
    modifier: Modifier = Modifier,
    chatHistory: Chat?,
    userViewModel: UserViewModel
) {
    if (chatHistory != null) {
        for (message in chatHistory.messages) {
            when (message) {
                is UserMessage -> {
                    ChatHistoryItem(
                        message = message as UserMessage,
                        type = 0,
                        userViewModel = userViewModel
                    )
                }

                is AssistantMessage -> {
                    ChatHistoryItem(
                        message = message as AssistantMessage,
                        type = 1,
                        userViewModel = userViewModel
                    )
                }

                is ToolMessage -> {
                    ChatHistoryItem(
                        message = message as ToolMessage,
                        type = 2,
                        userViewModel = userViewModel
                    )
                }

                else -> {
                    ChatHistoryItem(
                        message = message as SystemMessage,
                        type = 3,
                        userViewModel = userViewModel
                    )
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

    }

}

