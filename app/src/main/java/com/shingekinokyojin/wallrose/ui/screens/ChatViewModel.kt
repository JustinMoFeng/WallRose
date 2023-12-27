package com.shingekinokyojin.wallrose.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ChatViewModel : ViewModel() {

    // LiveData for chat messages
    private val _messages = MutableLiveData<List<String>>()
    val messages: LiveData<List<String>> = _messages
    var currentMessage by mutableStateOf("")

    fun sendMessage(message: String) {
        // Assuming _messages.value is not null.
        // In a real application, null checks and thread safety should be considered.
        val updatedMessages = _messages.value.orEmpty().toMutableList().apply {
            add(message)
        }
        _messages.value = updatedMessages
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }

    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/greeting_stream"
    fun getMessages() {
        Log.d("ChatViewModel", "Listening to $url")
        viewModelScope.launch(Dispatchers.IO) {
            currentMessage = ""
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
                                    Log.d("ChatViewModel", data)
                                    currentMessage += data
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ChatViewModel()
            }
        }
    }
}
