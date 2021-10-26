package github.leavesc.compose_chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import github.leavesc.compose_chat.logic.HomeViewModel
import github.leavesc.compose_chat.model.AppTheme
import github.leavesc.compose_chat.model.HomeScreenTab
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.chat.ChatScreen
import github.leavesc.compose_chat.ui.friend.FriendProfileScreen
import github.leavesc.compose_chat.ui.home.HomeScreen
import github.leavesc.compose_chat.ui.login.LoginScreen
import github.leavesc.compose_chat.ui.theme.ChatTheme
import github.leavesc.compose_chat.ui.weigets.SetSystemBarsColor

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
                SetSystemBarsColor()
                NavigationView(appTheme = appTheme) {
                    homeViewModel.switchToNextTheme()
                }
            }
        }
    }

    @Composable
    private fun NavigationView(appTheme: AppTheme, switchToNextTheme: () -> Unit) {
        val navController = rememberAnimatedNavController()
        var screenSelected by remember {
            mutableStateOf(HomeScreenTab.Conversation)
        }
        ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
            AnimatedNavHost(
                navController = navController,
                startDestination = Screen.LoginScreen.route
            ) {
                animatedComposable(
                    screen = Screen.LoginScreen,
                ) {
                    LoginScreen(navController = navController)
                }
                animatedComposable(screen = Screen.HomeScreen) {
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
                animatedComposable(screen = Screen.FriendProfileScreen()) { backStackEntry ->
                    FriendProfileScreen(
                        navController = navController,
                        friendId = Screen.FriendProfileScreen.getArgument(backStackEntry)
                    )
                }
                animatedComposable(screen = Screen.ChatScreen()) { backStackEntry ->
                    val listState = rememberLazyListState()
                    ChatScreen(
                        navController = navController,
                        listState = listState,
                        friendId = Screen.ChatScreen.getArgument(backStackEntry),
                    )
                }
            }
        }
    }

    private fun NavGraphBuilder.animatedComposable(
        screen: Screen,
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route = screen.route,
            enterTransition = { _, _ ->
                slideInHorizontally(
                    initialOffsetX = {
                        -it
                    },
                    animationSpec = tween(400)
                ) + fadeIn(initialAlpha = 0.6f, animationSpec = tween(400))
            },
            exitTransition = { _, _ ->
                fadeOut(targetAlpha = 0.9f, animationSpec = tween(400))
            },
            popEnterTransition = { _, _ ->
                slideInHorizontally(
                    initialOffsetX = {
                        0
                    },
                    animationSpec = tween(10)
                ) + fadeIn(initialAlpha = 0.7f, animationSpec = tween(200))
            },
            popExitTransition = { _, _ ->
                slideOutHorizontally(targetOffsetX = {
                    it
                }, animationSpec = tween(400))
            },
        ) { backStackEntry ->
            content(backStackEntry)
        }
    }

}