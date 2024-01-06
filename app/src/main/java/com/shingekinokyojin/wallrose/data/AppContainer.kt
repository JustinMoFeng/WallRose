package com.shingekinokyojin.wallrose.data

import com.shingekinokyojin.wallrose.network.AuthenticateApiService
import com.shingekinokyojin.wallrose.network.ChatApiService
import com.shingekinokyojin.wallrose.network.UserApiService
import com.shingekinokyojin.wallrose.utils.SharedPreferencesManager
import okhttp3.OkHttpClient

interface AppContainer {
    val chatsRepository: ChatsRepository
    val userRepository: UserRepository
    val baseUrl: String
}

class DefaultAppContainer : AppContainer {

    companion object{
        val url = "http://wallrose.huox3.cn:8000"
    }
    override val baseUrl: String
        get() = "http://wallrose.huox3.cn:8000"

    /**
     * okhttp client for creating api calls
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithHeaders = originalRequest.newBuilder()
                    .header("Authorization", "Bearer "+SharedPreferencesManager.getToken()) // 替换为你想要的头部名和值
                    .build()
                chain.proceed(requestWithHeaders)
            }
            .build()
    }

    /**
     * api service for chats
     */
    private val chatApiService: ChatApiService by lazy {
        ChatApiService(baseUrl, okHttpClient)
    }

    /**
     * api service for users
     */
    private val userApiService: UserApiService by lazy {
        UserApiService(baseUrl, okHttpClient)
    }

    /**
     * DI implementation for Chats repository
     */
    override val chatsRepository: ChatsRepository by lazy {
        NetworkChatsRepository(chatApiService)
    }

    /**
     * api service for authentication
     */
    private val authenticateApiService: AuthenticateApiService by lazy {
        AuthenticateApiService(baseUrl, okHttpClient)
    }

    /**
     * DI implementation for User repository
     */
    override val userRepository: UserRepository by lazy {
        NetworkUserRepository(authenticateApiService, userApiService)
    }
}