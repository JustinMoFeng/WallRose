import okhttp3.*
import kotlinx.coroutines.*
import java.io.IOException

class GptApiResponse(private val url: String) {
    private val client = OkHttpClient()

    fun startListening() = CoroutineScope(Dispatchers.IO).launch {
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
        // 处理接收到的数据
        println(data)
    }
}

fun main() {
    val greetingStreamClient = GptApiResponse("http://your-backend-url/greeting_stream") // 替换为您的后端 URL
    greetingStreamClient.startListening()

    // 让程序保持运行，以接收流式数据
    runBlocking {
        delay(10000)
    }
}
