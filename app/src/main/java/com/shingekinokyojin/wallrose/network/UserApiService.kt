package com.shingekinokyojin.wallrose.network

import android.util.Log
import com.shingekinokyojin.wallrose.model.AuthenticateErrorResult
import com.shingekinokyojin.wallrose.model.UserInfoResult
import com.shingekinokyojin.wallrose.ui.screens.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class UserApiService(
    private val url: String,
    private val okHttpClient: OkHttpClient
) {
    suspend fun getMeInfo():Array<String>  = withContext(
        Dispatchers.IO) {
        val request = okhttp3.Request.Builder()
            .url("$url/users/me")
            .get()
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    val errorResponse = Json.decodeFromString<AuthenticateErrorResult>(response.body!!.string())
                    Log.e("UserApiService", "Error: ${errorResponse.detail}")
                    arrayOf(errorResponse.detail)
                }else {
                    val responseBody = Json.decodeFromString<UserInfoResult>(response.body!!.string())
                    Log.d("UserApiService", "Success: ${responseBody.toString()}")

                    arrayOf("true", responseBody.username, responseBody.nickname, responseBody.avatarUrl ?: "")
                }
            }
        }catch (e: Exception){
            Log.e("UserApiService", "Error: ${e.message}")
            arrayOf(e.message!!)
        }
    }
}