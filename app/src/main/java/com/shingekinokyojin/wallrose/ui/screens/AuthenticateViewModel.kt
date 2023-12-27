package com.shingekinokyojin.wallrose.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthenticateViewModel : ViewModel(){
    var username by mutableStateOf("")
    var password by mutableStateOf("")
}