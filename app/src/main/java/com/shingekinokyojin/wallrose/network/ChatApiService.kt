package com.shingekinokyojin.wallrose.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

private const val  BASE_URL = "http://127.0.0.1:8000"
class ChatApiService(private val url: String) {
    private val client = OkHttpClient()

    fun startListening() = CoroutineScope(Dispatchers.IO).launch {
        println("Listening to $url")
        val request = Request.Builder()
            .url(url)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                response.body?.let { responseBody ->
                    responseBody.source().use { source ->
                        while (!source.exhausted()) {
                            val line = source.readUtf8Line() ?: break
                            if (line.startsWith("data:")) {
                                val data = line.removePrefix("data:")
                                processEvent(data)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processEvent(data: String) {
        // 处理接收到的数据，不要换行
        print(data)
    }
}