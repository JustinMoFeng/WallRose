package com.shingekinokyojin.wallrose.network

import android.util.Log
import com.shingekinokyojin.wallrose.model.AuthenticateErrorResult
import com.shingekinokyojin.wallrose.model.ImageUploadResult
import com.shingekinokyojin.wallrose.model.UserInfoResult
import com.shingekinokyojin.wallrose.ui.screens.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

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

    suspend fun uploadImage(file: File): String = withContext(
        Dispatchers.IO) {
        val request = okhttp3.Request.Builder()
            .url("$url/upload_image")
            .post(
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.name, file.asRequestBody())
                    .build()
            )
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    val errorResponse = Json.decodeFromString<AuthenticateErrorResult>(response.body!!.string())
                    Log.e("UserApiService", "Error: ${errorResponse.detail}")
                    errorResponse.detail
                }else {
                    val responseBody = Json.decodeFromString<ImageUploadResult>(response.body!!.string())
                    Log.d("UserApiService", "Success: $responseBody")
                    responseBody.message
                }
            }
        }catch (e: Exception){
            Log.e("UserApiService", "Error: ${e.message}")
            e.message!!
        }
    }
}