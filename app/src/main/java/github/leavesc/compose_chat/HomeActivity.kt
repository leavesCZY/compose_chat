package github.leavesc.compose_chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import github.leavesc.compose_chat.base.model.ServerState
import github.leavesc.compose_chat.cache.AccountCache
import github.leavesc.compose_chat.extend.navToLogin
import github.leavesc.compose_chat.logic.AppViewModel
import github.leavesc.compose_chat.model.AppTheme
import github.leavesc.compose_chat.model.HomeScreenTab
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.chat.ChatScreen
import github.leavesc.compose_chat.ui.chat.GroupProfileScreen
import github.leavesc.compose_chat.ui.friend.FriendProfileScreen
import github.leavesc.compose_chat.ui.home.HomeScreen
import github.leavesc.compose_chat.ui.login.LoginScreen
import github.leavesc.compose_chat.ui.theme.ChatTheme
import github.leavesc.compose_chat.ui.weigets.SetSystemBarsColor
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.flow.collect

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
            val appViewModel = viewModel<AppViewModel>()
            val appTheme by appViewModel.appTheme.collectAsState()
            val switchToNextTheme = remember {
                {
                    appViewModel.switchToNextTheme()
                }
            }
            val navController = rememberAnimatedNavController()
            LaunchedEffect(key1 = Unit) {
                appViewModel.serverConnectState.collect {
                    when (it) {
                        ServerState.KickedOffline -> {
                            showToast("本账号已在其它客户端登陆，请重新登陆")
                            AccountCache.onUserLogout()
                            navController.navToLogin()
                        }
                        ServerState.Logout -> {
                            navController.navToLogin()
                        }
                        else -> {
                            showToast("Connect State Changed : $it")
                        }
                    }
                }
            }
            ChatTheme(appTheme = appTheme) {
                SetSystemBarsColor(
                    statusBarColor = Color.Transparent,
                    navigationBarColor = MaterialTheme.colors.background,
                    statusBarDarkIcons = appTheme == AppTheme.Light,
                    navigationDarkIcons = appTheme != AppTheme.Dark
                )
                NavigationView(
                    navController = navController,
                    appTheme = appTheme,
                    switchToNextTheme = switchToNextTheme
                )
            }
        }
    }

    @Composable
    private fun NavigationView(
        navController: NavHostController,
        appTheme: AppTheme,
        switchToNextTheme: () -> Unit
    ) {
        var homeScreenSelected by remember {
            mutableStateOf(HomeScreenTab.Conversation)
        }
        ProvideWindowInsets {
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
                        homeTabSelected = homeScreenSelected,
                        onHomeTabSelected = {
                            homeScreenSelected = it
                        }
                    )
                }
                animatedComposable(screen = Screen.FriendProfileScreen) { backStackEntry ->
                    FriendProfileScreen(
                        navController = navController,
                        friendId = Screen.FriendProfileScreen.getArgument(backStackEntry)
                    )
                }
                animatedComposable(screen = Screen.ChatScreen) { backStackEntry ->
                    val listState = rememberLazyListState()
                    ChatScreen(
                        navController = navController,
                        listState = listState,
                        chat = Screen.ChatScreen.getArgument(backStackEntry),
                    )
                }
                animatedComposable(screen = Screen.GroupProfileScreen) { backStackEntry ->
                    GroupProfileScreen(
                        navController = navController,
                        groupId = Screen.GroupProfileScreen.getArgument(backStackEntry),
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
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = {
                        -it
                    },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(targetAlpha = 0.9f, animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = {
                        0
                    },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = {
                    it
                }, animationSpec = tween(300))
            },
        ) { backStackEntry ->
            content(backStackEntry)
        }
    }

}