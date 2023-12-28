package com.shingekinokyojin.wallrose.network

import android.util.Log
import com.shingekinokyojin.wallrose.model.ChatEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ChatApiService(
    private val url: String,
    private val okHttpClient: OkHttpClient
) {
    fun sendMessage(message: String) : Flow<ChatEvent> {
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
}