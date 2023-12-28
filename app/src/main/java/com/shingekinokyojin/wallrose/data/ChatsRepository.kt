package com.shingekinokyojin.wallrose.data

import com.shingekinokyojin.wallrose.model.ChatEvent
import com.shingekinokyojin.wallrose.network.ChatApiService
import kotlinx.coroutines.flow.Flow

interface ChatsRepository {
    fun getGreeting(message: String) : Flow<ChatEvent>
    fun sendMessage(message: String, chatId: String): Flow<ChatEvent>
    suspend fun createChat(): String?
    suspend fun returnMessage(returnStr: String, currentChatId: String, toolCallId: String, toolName: String)
}

class NetworkChatsRepository(private val chatApiService: ChatApiService) : ChatsRepository {
    override fun getGreeting(message: String): Flow<ChatEvent> {
        return chatApiService.getGreeting(message)
    }

    override fun sendMessage(message: String,chatId: String): Flow<ChatEvent> {
        return chatApiService.sendMessage(message,chatId)
    }

     override suspend fun createChat(): String? {
        return chatApiService.createChat()
     }

    override suspend fun returnMessage(returnStr: String, currentChatId: String, toolCallId: String, toolName: String) {
        return chatApiService.returnMessage(returnStr,currentChatId,toolCallId,toolName)
    }
}