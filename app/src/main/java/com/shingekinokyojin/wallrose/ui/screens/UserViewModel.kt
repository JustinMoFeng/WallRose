package com.shingekinokyojin.wallrose.ui.screens


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.shingekinokyojin.wallrose.WallRoseApplication
import com.shingekinokyojin.wallrose.data.UserRepository
import com.shingekinokyojin.wallrose.model.AssistantMessage
import com.shingekinokyojin.wallrose.model.Chat
import com.shingekinokyojin.wallrose.model.UserMessage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UserViewModel(
    private val userRepository: UserRepository
) :ViewModel() {

    var myUsername by mutableStateOf("")
    var myNickname by mutableStateOf("")
    var myAvatarUrl by mutableStateOf("")
    var userInfoState by mutableStateOf("")

    var reviseUserInfoState by mutableStateOf("")
    var reviseUserInfo by mutableStateOf("")
    var newNickname by mutableStateOf("")
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var newPasswordAgain by mutableStateOf("")

    var chatHistory: List<String> by mutableStateOf(listOf())
    var chosenChatHistory: Chat? by mutableStateOf(null)

    fun getChatHistory() {
        viewModelScope.launch {
            val string = userRepository.getChatHistory()
            Log.d("UserViewModel", "getChatHistory: $string")
            for (chat in string) {
                val messages = chat.messages
                if(messages.size>=2){
                    val secondMessage = messages[1]
                    if(secondMessage is UserMessage){
                        chatHistory = chatHistory + secondMessage.content
                    }else if(secondMessage is AssistantMessage){
                        chatHistory = chatHistory + secondMessage.content.toString()
                    }
                }
            }

        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            val stringArr = userRepository.getMeInfo()
            userInfoState = stringArr[0]
            if(stringArr[0] == "true") {
                myUsername = stringArr[1]
                myNickname = stringArr[2]
                myAvatarUrl = stringArr[3]
            }
        }
    }
    
    fun uploadImage(context:Context, imageURL: Uri){
        viewModelScope.launch {
            val file = uriToFile(context, imageURL)
            val string = userRepository.uploadImage(file!!)
            myAvatarUrl = string
        }
    }

    @SuppressLint("Recycle")
    fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            // 在应用的缓存目录中创建临时文件
            val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
            tempFile.deleteOnExit()
            FileOutputStream(tempFile).use { fileOutputStream ->
                inputStream.copyTo(fileOutputStream)
            }
            return tempFile
        }
        return null
    }
    
    fun reviseNickname() {
        viewModelScope.launch {
            val string = userRepository.reviseNickname(newNickname)
            reviseUserInfoState = string
            if(string=="true") {
                myNickname = newNickname
            }
        }
    }

    fun revisePassword() {
        if (newPassword != newPasswordAgain) {
            reviseUserInfoState = "两次输入的密码不一致"
            return
        }
        viewModelScope.launch {
            val string = userRepository.revisePassword(oldPassword, newPassword)
            reviseUserInfoState = string
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WallRoseApplication)
                val userRepository = application.container.userRepository
                UserViewModel(userRepository)
            }
        }
    }
}