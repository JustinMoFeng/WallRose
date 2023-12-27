package com.shingekinokyojin.wallrose.navigator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.ui.composables.chat.ChatPage
import com.shingekinokyojin.wallrose.ui.composables.profile.ProfilePage
import com.shingekinokyojin.wallrose.ui.screens.ChatViewModel

@Composable
fun WallRoseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = RouteConfig.ROUTE_CHAT
){

    val chatViewModel : ChatViewModel = viewModel(factory = ChatViewModel.Factory)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ){
        composable(RouteConfig.ROUTE_CHAT){
            ChatPage(
                chatViewModel = chatViewModel,
                navController = navController
            )
        }
        
        composable(RouteConfig.ROUTE_PROFILE){
            ProfilePage(navController = navController)
        }
    }
}