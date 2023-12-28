package com.shingekinokyojin.wallrose.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import com.shingekinokyojin.wallrose.WallRoseApplication
import com.shingekinokyojin.wallrose.data.UserRepository
import kotlinx.coroutines.launch

class AuthenticateViewModel(
    private val authenticateRepository: UserRepository
) : ViewModel(){



    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var nickname by mutableStateOf("")

    var registerState by mutableStateOf("")
    var loginState by mutableStateOf("")

    fun register() {
        viewModelScope.launch {
            val string = authenticateRepository.register(username, password, nickname)
            registerState = string.toString()
        }
    }

    fun login() {
        viewModelScope.launch {
            val string = authenticateRepository.login(username, password)
            loginState = string.toString()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as WallRoseApplication)
                val authenticateRepository = application.container.userRepository
                AuthenticateViewModel(authenticateRepository)
            }
        }
    }
}