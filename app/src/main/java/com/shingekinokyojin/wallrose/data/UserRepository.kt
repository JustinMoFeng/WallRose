package com.shingekinokyojin.wallrose.data

import com.shingekinokyojin.wallrose.network.AuthenticateApiService
import com.shingekinokyojin.wallrose.network.UserApiService
import com.shingekinokyojin.wallrose.ui.screens.UserViewModel

interface UserRepository {
    suspend fun register(username: String, password: String, nickname: String):String
    suspend fun login(username: String, password: String):String

    suspend fun getMeInfo():Array<String>
}

class NetworkUserRepository(
    private val authenticateApiService: AuthenticateApiService,
    private val userApiService: UserApiService
) : UserRepository {
    override suspend fun register(username: String, password: String, nickname: String):String {
        return authenticateApiService.register(username, password, nickname)
    }

    override suspend fun login(username: String, password: String):String {
        return authenticateApiService.login(username, password)
    }

    override suspend fun getMeInfo():Array<String> {
        return userApiService.getMeInfo()
    }
}