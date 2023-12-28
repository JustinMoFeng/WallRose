package com.shingekinokyojin.wallrose.network

import android.util.Log
import com.shingekinokyojin.wallrose.model.AuthenticateErrorResult
import com.shingekinokyojin.wallrose.model.AuthenticateLoginResult
import com.shingekinokyojin.wallrose.utils.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AuthenticateApiService (
    private val url: String,
    private val okHttpClient: OkHttpClient
){

    suspend fun register(username: String, password: String, nickname: String): String = withContext(
        Dispatchers.IO) {
        val jsonObject = JSONObject().apply {
            put("username", username)
            put("password", password)
            put("nickname", nickname)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$url/register")
            .post(requestBody)
            .build()


        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    val errorResponse = Json.decodeFromString<AuthenticateErrorResult>(response.body!!.string())
                    Log.e("AuthenticateApiService", "Error: ${errorResponse.detail}")
                    errorResponse.detail
                }else {
                    "true"
                }
            }

        } catch (e: Exception) {
            Log.e("AuthenticateApiService", "Error: ${e.message}")
            e.message!!
        }
    }

    suspend fun login(username: String,password: String): String = withContext(
        Dispatchers.IO
    ){
        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("$url/login") // 替换为你的 API 地址
            .post(requestBody)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    val errorResponse = Json.decodeFromString<AuthenticateErrorResult>(response.body!!.string())
                    Log.e("AuthenticateApiService", "Error: ${errorResponse.detail}")
                    errorResponse.detail
                }else {
                    val loginResponse = Json.decodeFromString<AuthenticateLoginResult>(response.body!!.string())
                    Log.d("AuthenticateApiService", "Success: ${loginResponse.access_token}")
                    SharedPreferencesManager.saveToken(loginResponse.access_token)
                    "true"
                }
            }

        } catch (e: Exception) {
            Log.e("AuthenticateApiService", "Error: ${e.message}")
            e.message!!
        }

    }



}