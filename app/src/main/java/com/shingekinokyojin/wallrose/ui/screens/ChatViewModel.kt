package com.shingekinokyojin.wallrose.ui.screens

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
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
import com.shingekinokyojin.wallrose.model.AlarmArgument
import com.shingekinokyojin.wallrose.model.Chat
import com.shingekinokyojin.wallrose.model.ChatEvent
import com.shingekinokyojin.wallrose.model.Function
import com.shingekinokyojin.wallrose.model.ToolCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ChatViewModel(
    private val chatsRepository: ChatsRepository,
    private val context: Context
) : ViewModel() {

    // LiveData for chat messages
    private val _messages = MutableLiveData<List<String>>()
    val messages: LiveData<List<String>> = _messages
    var responding by mutableStateOf(false)
    var currentMessage by mutableStateOf("")
    var inputMessage by mutableStateOf("")
    var chatStatus by mutableStateOf("")

    var currentChatId by mutableStateOf("")

    fun sendMessage(message:String){

        if (responding) {
            return
        }
        var toolFlag = false
        responding = true
        inputMessage = ""
        currentMessage = "..."
        Log.d("ChatViewModel", "Sending $inputMessage")
        viewModelScope.launch(Dispatchers.IO) {
            if(currentChatId == ""){
                currentChatId = chatsRepository.createChat().toString()
                Log.d("ChatViewModel", "Created chat $currentChatId")
            }
            chatsRepository.sendMessage(message,currentChatId).collect() { chatEvent ->
                when(chatEvent.event){
                    "error" -> {
                        chatStatus = "error"
                        currentMessage = chatEvent.data
                        return@collect
                    }
                    "message" -> {
                        if (currentMessage == "...") {
                            currentMessage = ""
                        }
                        Log.d("ChatViewModel", "Received $chatEvent")
                        currentMessage += chatEvent.data
                    }
                    "tool_call" -> {
                        val toolObj = Json.decodeFromString<ToolCall>(chatEvent.data)
                        toolFlag = true
                        var returnStr = when(toolObj.function.name){
                            "set_alarm" -> setAlarmClock(toolObj.function.arguments)
                            else -> "unknown function"
                        }

                        chatsRepository.returnMessage(returnStr,currentChatId,toolObj.id,toolObj.function.name)

                        Log.d("ChatViewModel", "Received $chatEvent")

                    }
                    "chat_id" -> {
                        currentChatId = chatEvent.data
                    }
                }
            }

            if(toolFlag){
                responding = false
                sendMessage("")
            }

            responding = false
        }
    }

    fun setAlarmClock(arguments: String):String {
        try {
            val AlarmArg = Json.decodeFromString<AlarmArgument>(arguments)
            Log.d("ChatViewModel", "Received $AlarmArg")

            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            val time = AlarmArg.time
            val hour = time.split(":")[0].toInt()
            val minute = time.split(":")[1].toInt()
            intent.apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, "米奇妙妙屋")
                putExtra(AlarmClock.EXTRA_VIBRATE, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return "success"
        }catch (e: Exception){
            Log.e("ChatViewModel", "Error: $e")
            return "fail"
        }
    }



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
                ChatViewModel(chatsRepository,application.applicationContext)
            }
        }
    }
}
