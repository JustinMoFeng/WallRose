package com.shingekinokyojin.wallrose.network

import android.util.Log
import com.shingekinokyojin.wallrose.model.AuthenticateErrorResult
import com.shingekinokyojin.wallrose.model.Chat
import com.shingekinokyojin.wallrose.model.ChatEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull.content
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ChatApiService(
    private val url: String,
    private val okHttpClient: OkHttpClient
) {
    fun getGreeting(message: String) : Flow<ChatEvent> {
        return flow {
            val request = Request.Builder()
                .url("$url/greeting_stream?message=${message}")
                .build()
            try {
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful){
                        if(response.code==401){
                            emit(ChatEvent("error", "Unauthorized"))
                        }
                        throw IOException("Unexpected code $response")
                    }

                    // SSE
                    val source = response.body!!.source()
                    var event = ""
                    var data = ""
                    while (!source.exhausted()) {
                        val line = source.readUtf8LineStrict()
                        if (line.startsWith("event: ")) {
                            event = line.substring(7)
                        } else if (line.startsWith("data: ")) {
                            data = line.substring(6)
                        } else if (line == "") {
                            emit(ChatEvent(event, data))
                            event = ""
                            data = ""
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatApiService", "Error: $e")
            }
        }
    }

    fun sendMessage(message: String,chatId:String): Flow<ChatEvent> {

        return flow {
            val request = Request.Builder()
                .url("$url/chats/$chatId/send?content=${message}")
                .post("".toRequestBody(null))
                .build()
            try {
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful){
                        if(response.code==401){
                            emit(ChatEvent("error", "Unauthorized"))
                        }
                        throw IOException("Unexpected code $response")
                    }

                    // SSE
                    val source = response.body!!.source()
                    var event = ""
                    var data = ""
                    while (!source.exhausted()) {
                        val line = source.readUtf8LineStrict()
                        if (line.startsWith("event: ")) {
                            event = line.substring(7)
                        } else if (line.startsWith("data: ")) {
                            data = line.substring(6)
                        } else if (line == "") {
                            emit(ChatEvent(event, data))
                            event = ""
                            data = ""
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatApiService", "Error: $e")
            }
        }
    }

    suspend fun createChat(): String? = withContext(Dispatchers.IO) {

        val request = Request.Builder()
            .url("$url/chats/create")
            .post("".toRequestBody(null))
            .build()

        try{
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    if(response.code==401){
                        throw IOException("Unauthorized")
                    }
                    throw IOException("Unexpected code $response")
                }
                val responseBody = Json.decodeFromString<Chat>(response.body!!.string())
                Log.d("ChatApiService", "Success: $responseBody")
                responseBody._id
            }
        }catch (e: Exception){
            Log.e("ChatApiService", "Error: $e")
            ""
        }

    }

    fun returnMessage(returnStr: String, currentChatId: String, toolCallId: String, toolName: String) {

        val request = Request.Builder()
            .url("$url/chats/$currentChatId/return?tool_call_id=$toolCallId&tool_name=$toolName&content=$returnStr")
            .post("".toRequestBody(null))
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful){
                    if(response.code==401){
                        throw IOException("Unauthorized")
                    }
                    throw IOException("Unexpected code $response")
                }
            }
        } catch (e: Exception) {
            Log.e("ChatApiService", "Error: $e")
        }

    }

}