package com.shingekinokyojin.wallrose.ui.screens

import android.content.Context
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
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) :ViewModel() {

    var myUsername by mutableStateOf("")
    var myNickname by mutableStateOf("")
    var myAvatarUrl by mutableStateOf("")
    var userInfoStatus by mutableStateOf("")

    var reviseUserInfo by mutableStateOf("")


    var reviseNickname by mutableStateOf("")
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var newPasswordAgain by mutableStateOf("")

    fun getUserInfo() {
        viewModelScope.launch {
            val stringArr = userRepository.getMeInfo()
            userInfoStatus = stringArr[0]
            if(stringArr[0] == "true") {
                myUsername = stringArr[1]
                myNickname = stringArr[2]
                myAvatarUrl = stringArr[3]
            }
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