package com.shingekinokyojin.wallrose.navigator

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shingekinokyojin.wallrose.WallRoseApplication
import com.shingekinokyojin.wallrose.config.RouteConfig
import com.shingekinokyojin.wallrose.ui.composables.authenticate.LoginPage
import com.shingekinokyojin.wallrose.ui.composables.authenticate.RegisterPage
import com.shingekinokyojin.wallrose.ui.composables.chat.ChatHistoryPage
import com.shingekinokyojin.wallrose.ui.composables.chat.ChatPage
import com.shingekinokyojin.wallrose.ui.composables.profile.ProfileDetailPage
import com.shingekinokyojin.wallrose.ui.composables.profile.ProfileModificationPage
import com.shingekinokyojin.wallrose.ui.composables.profile.ProfilePage
import com.shingekinokyojin.wallrose.ui.screens.AuthenticateViewModel
import com.shingekinokyojin.wallrose.ui.screens.ChatViewModel
import com.shingekinokyojin.wallrose.ui.screens.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallRoseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = RouteConfig.ROUTE_CHAT,
){
    val url = WallRoseApplication.instance.container.baseUrl
    val chatViewModel : ChatViewModel = viewModel(factory = ChatViewModel.Factory)
    val authenticateViewModel : AuthenticateViewModel = viewModel(factory = AuthenticateViewModel.Factory)
    val userViewModel : UserViewModel = viewModel(factory = UserViewModel.Factory)
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ){
        composable(RouteConfig.ROUTE_CHAT){
            ChatPage(
                chatViewModel = chatViewModel,
                navController = navController,
                userViewModel = userViewModel,
                drawerState = drawerState
            )
        }
        
        composable(RouteConfig.ROUTE_PROFILE){
            ProfilePage(
                navController = navController,
                userViewModel = userViewModel,
                url = url,
                chatViewModel = chatViewModel,
                drawerState = drawerState
            )
        }

        composable(RouteConfig.ROUTE_LOGIN){
            LoginPage(
                authenticateViewModel = authenticateViewModel,
                navController = navController
            )
        }

        composable(RouteConfig.ROUTE_REGISTER){
            RegisterPage(
                authenticateViewModel = authenticateViewModel,
                navController = navController,
            )
        }

        composable(RouteConfig.ROUTE_PROFILE_DETAIL){
            ProfileDetailPage(
                navController = navController,
                userViewModel = userViewModel,
                url = url
            )
        }

        composable(RouteConfig.ROUTE_PROFILE_MODIFICATION){
            ProfileModificationPage(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable(RouteConfig.ROUTE_CHAT_HISTORY){
            ChatHistoryPage(navController = navController
                , userViewModel = userViewModel,
                chatViewModel = chatViewModel
            )
        }
    }
}