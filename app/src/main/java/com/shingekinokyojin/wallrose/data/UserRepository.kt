package com.shingekinokyojin.wallrose.data

import com.shingekinokyojin.wallrose.network.AuthenticateApiService
import com.shingekinokyojin.wallrose.network.UserApiService
import java.io.File

interface UserRepository {
    suspend fun register(username: String, password: String, nickname: String):String
    suspend fun login(username: String, password: String):String
    suspend fun getMeInfo():Array<String>
    suspend fun uploadImage(file: File): String
    suspend fun reviseNickname(nickname: String):String
    suspend fun revisePassword(oldPassword: String, newPassword: String):String
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
    
    override suspend fun uploadImage(file: File): String {
        return userApiService.uploadImage(file)
    }

    override suspend fun reviseNickname(nickname: String):String {
        return userApiService.reviseNickname(nickname)
    }

    override suspend fun revisePassword(oldPassword: String, newPassword: String):String {
        return userApiService.revisePassword(oldPassword, newPassword)
    }
}