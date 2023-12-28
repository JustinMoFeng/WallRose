package com.shingekinokyojin.wallrose.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.shingekinokyojin.wallrose.WallRoseApplication
import com.shingekinokyojin.wallrose.data.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(
    private val UserRepository: UserRepository
) :ViewModel() {

    var myUsername by mutableStateOf("")
    var myNickname by mutableStateOf("")
    var myAvatarUrl by mutableStateOf("")
    var userInfoStatus by mutableStateOf("")

    fun getUserInfo() {
        viewModelScope.launch {
            val stringArr = UserRepository.getMeInfo()
            userInfoStatus = stringArr[0]
            if(stringArr[0] == "true") {
                myUsername = stringArr[0]
                myNickname = stringArr[1]
                myAvatarUrl = stringArr[2]
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WallRoseApplication)
                val authenticateRepository = application.container.userRepository
                UserViewModel(authenticateRepository)
            }
        }
    }
}