package com.shingekinokyojin.wallrose.navigator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.page.chat.ChatPage
import com.shingekinokyojin.wallrose.page.profile.ProfilePage

@Composable
fun WallRoseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = RouteConfig.ROUTE_CHAT
){
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ){
        composable(RouteConfig.ROUTE_CHAT){
            ChatPage(navController = navController)
        }
        
        composable(RouteConfig.ROUTE_PROFILE){
            ProfilePage(navController = navController)
        }
    }
}