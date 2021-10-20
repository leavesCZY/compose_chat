package github.leavesc.compose_chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import github.leavesc.compose_chat.logic.HomeViewModel
import github.leavesc.compose_chat.model.AppTheme
import github.leavesc.compose_chat.model.HomeScreenTab
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.chat.ChatScreen
import github.leavesc.compose_chat.ui.common.SetSystemBarsColor
import github.leavesc.compose_chat.ui.friend.FriendProfileScreen
import github.leavesc.compose_chat.ui.home.HomeScreen
import github.leavesc.compose_chat.ui.login.LoginScreen
import github.leavesc.compose_chat.ui.theme.ChatTheme

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
            val homeViewModel = viewModel<HomeViewModel>()
            val appTheme by homeViewModel.appTheme.collectAsState()
            ChatTheme(appTheme = appTheme) {
                SetSystemBarsColor(
                    key = appTheme,
                    statusBarColor = Color.Transparent,
                    navigationBarColor = Color.Transparent
                )
                NavigationView(appTheme = appTheme) {
                    homeViewModel.switchToNextTheme()
                }
            }
        }
    }

    @Composable
    private fun NavigationView(appTheme: AppTheme, switchToNextTheme: () -> Unit) {
        val navController = rememberNavController()
        var screenSelected by remember {
            mutableStateOf(HomeScreenTab.Conversation)
        }
        ProvideWindowInsets {
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
                        appTheme = appTheme,
                        switchToNextTheme = switchToNextTheme,
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

}