package com.shingekinokyojin.wallrose.data

import com.shingekinokyojin.wallrose.network.AuthenticateApiService

interface UserRepository {
    suspend fun register(username: String, password: String, nickname: String):String
//    fun login(username: String, password: String)
}

class NetworkUserRepository(private val authenticateApiService: AuthenticateApiService) : UserRepository {
    override suspend fun register(username: String, password: String, nickname: String):String {
        return authenticateApiService.register(username, password, nickname)
    }

//    override fun login(username: String, password: String) {
//        authenticateApiService.login(username, password)
//    }
}