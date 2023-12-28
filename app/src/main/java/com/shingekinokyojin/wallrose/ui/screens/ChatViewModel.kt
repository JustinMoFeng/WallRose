package com.shingekinokyojin.wallrose.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.shingekinokyojin.wallrose.WallRoseApplication
import com.shingekinokyojin.wallrose.data.ChatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatsRepository: ChatsRepository
) : ViewModel() {

    // LiveData for chat messages
    private val _messages = MutableLiveData<List<String>>()
    val messages: LiveData<List<String>> = _messages
    var responding by mutableStateOf(false)
    var currentMessage by mutableStateOf("")
    var inputMessage by mutableStateOf("")
    var chatStatus by mutableStateOf("")

    fun clearMessages() {
        _messages.value = emptyList()
    }

    fun getGreeting() {
        if (responding) {
            return
        }
        responding = true
        inputMessage = ""
        currentMessage = "..."
        Log.d("ChatViewModel", "Sending $inputMessage")
        viewModelScope.launch(Dispatchers.IO) {
            chatsRepository.getGreeting(inputMessage).collect() { chatEvent ->
                if(chatEvent.event == "error") {
                    chatStatus = "error"
                    currentMessage = chatEvent.data
                    return@collect
                }else if (currentMessage == "...") {
                    currentMessage = ""
                }
                Log.d("ChatViewModel", "Received $chatEvent")
                currentMessage += chatEvent.data
            }
            responding = false
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WallRoseApplication)
                val chatsRepository = application.container.chatsRepository
                ChatViewModel(chatsRepository)
            }
        }
    }
}
