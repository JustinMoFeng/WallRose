package com.shingekinokyojin.wallrose.data

import com.shingekinokyojin.wallrose.network.ChatApiService
import okhttp3.OkHttpClient

interface AppContainer {
    val chatsRepository: ChatsRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "http://10.0.2.2:8000"

    /**
     * okhttp client for creating api calls
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    /**
     * api service for chats
     */
    private val chatApiService: ChatApiService by lazy {
        ChatApiService(baseUrl, okHttpClient)
    }

    /**
     * DI implementation for Chats repository
     */
    override val chatsRepository: ChatsRepository by lazy {
        NetworkChatsRepository(chatApiService)
    }
}