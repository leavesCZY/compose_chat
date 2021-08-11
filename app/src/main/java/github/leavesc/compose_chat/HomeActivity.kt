package github.leavesc.compose_chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import github.leavesc.compose_chat.model.HomeScreenTab
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.chat.ChatScreen
import github.leavesc.compose_chat.ui.friend.FriendProfileScreen
import github.leavesc.compose_chat.ui.home.HomeScreen
import github.leavesc.compose_chat.ui.login.LoginScreen

/**
 * @Author: leavesC
 * @Date: 2021/6/23 21:59
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            NavigationView()
        }
    }

    @Composable
    fun NavigationView() {
        val navController = rememberNavController()
        var screenSelected by remember {
            mutableStateOf(HomeScreenTab.Conversation)
        }
        NavHost(
            navController = navController,
            startDestination = Screen.LoginScreen.route
        ) {
            composable(Screen.LoginScreen.route) {
                LoginScreen(navController = navController)
            }
            composable(Screen.HomeScreen.route) {
                HomeScreen(
                    navController = navController,
                    screenSelected = screenSelected,
                    onTabSelected = {
                        screenSelected = it
                    }
                )
            }
            composable(Screen.FriendProfileScreen().route) { backStackEntry ->
                FriendProfileScreen(
                    navController = navController,
                    friendId = Screen.FriendProfileScreen.getArgument(backStackEntry)
                )
            }
            composable(Screen.ChatScreen().route) { backStackEntry ->
                ChatScreen(
                    navController = navController,
                    friendId = Screen.ChatScreen.getArgument(backStackEntry),
                )
            }
        }
    }

}