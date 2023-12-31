import okhttp3.*
import kotlinx.coroutines.*
import java.io.IOException

class GptApiResponse(private val url: String) {
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

fun main() {
    val greetingStreamClient = GptApiResponse("http://127.0.0.1:8000/greeting_stream")
    greetingStreamClient.startListening()
    val prompt = "你好，我是"
//    val gptStreamClient = GptApiResponse("http://127.0.0.1:8000/gpt_query?prompt=$prompt")
//    gptStreamClient.startListening()

    // 让程序保持运行，以接收流式数据
    runBlocking {
        delay(10000)
    }
}
