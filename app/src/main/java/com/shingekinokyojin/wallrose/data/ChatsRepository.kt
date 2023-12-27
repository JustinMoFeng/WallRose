package com.shingekinokyojin.wallrose.data

import com.shingekinokyojin.wallrose.model.ChatEvent
import com.shingekinokyojin.wallrose.network.ChatApiService
import kotlinx.coroutines.flow.Flow

interface ChatsRepository {
    fun sendMessage(message: String) : Flow<ChatEvent>
}

class NetworkChatsRepository(private val chatApiService: ChatApiService) : ChatsRepository {
    override fun sendMessage(message: String): Flow<ChatEvent> {
        return chatApiService.sendMessage(message)
    }
}