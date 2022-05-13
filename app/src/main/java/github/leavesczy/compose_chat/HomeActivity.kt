package github.leavesczy.compose_chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import github.leavesczy.compose_chat.cache.AccountCache
import github.leavesczy.compose_chat.common.model.ServerState
import github.leavesczy.compose_chat.extend.ProvideNavHostController
import github.leavesczy.compose_chat.extend.navToLogin
import github.leavesczy.compose_chat.logic.AppViewModel
import github.leavesczy.compose_chat.model.AppTheme
import github.leavesczy.compose_chat.model.HomePageTab
import github.leavesczy.compose_chat.model.Page
import github.leavesczy.compose_chat.ui.chat.ChatPage
import github.leavesczy.compose_chat.ui.common.PreviewImagePage
import github.leavesczy.compose_chat.ui.friend.FriendProfilePage
import github.leavesczy.compose_chat.ui.home.HomePage
import github.leavesczy.compose_chat.ui.login.LoginPage
import github.leavesczy.compose_chat.ui.profile.GroupProfilePage
import github.leavesczy.compose_chat.ui.profile.UpdateProfilePage
import github.leavesczy.compose_chat.ui.theme.ChatTheme
import github.leavesczy.compose_chat.ui.widgets.SystemBarColor
import github.leavesczy.compose_chat.utils.showToast

/**
 * @Author: leavesCZY
 * @Date: 2021/6/23 21:59
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val appViewModel = viewModel<AppViewModel>()
            val appTheme by appViewModel.appTheme.collectAsState()
            val navController = rememberAnimatedNavController()
            val localFocusManager = LocalFocusManager.current
            val localLifecycleOwner = LocalLifecycleOwner.current
            LaunchedEffect(key1 = Unit) {
                if (savedInstanceState != null) {
                    navController.navToLogin()
                }
                appViewModel.serverConnectState.collect {
                    when (it) {
                        ServerState.KickedOffline -> {
                            showToast("本账号已在其它客户端登陆，请重新登陆")
                            AccountCache.onUserLogout()
                            navController.navToLogin()
                        }
                        ServerState.Logout, ServerState.UserSigExpired -> {
                            navController.navToLogin()
                        }
                        else -> {
                            showToast("Connect State Changed : $it")
                        }
                    }
                }
            }
            DisposableEffect(key1 = Unit) {
                val observer = object : DefaultLifecycleObserver {
                    override fun onStop(owner: LifecycleOwner) {
                        super.onStop(owner)
                        localFocusManager.clearFocus(force = true)
                    }
                }
                localLifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    localLifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            ChatTheme(appTheme = appTheme) {
                SystemBarColor(appTheme = appTheme)
                NavigationView(
                    navController = navController,
                    appTheme = appTheme,
                    switchToNextTheme = {
                        appViewModel.switchToNextTheme()
                    }
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
        var homeTabSelected by rememberSaveable {
            mutableStateOf(HomePageTab.Conversation)
        }
        ProvideNavHostController(navHostController = navController) {
            AnimatedNavHost(
                modifier = Modifier.navigationBarsPadding(),
                navController = navController,
                startDestination = Page.LoginPage.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = {
                            -it
                        },
                        animationSpec = tween(300)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = {
                            it
                        },
                        animationSpec = tween(300)
                    )
                },
            ) {
                animatedComposable(page = Page.LoginPage) {
                    LoginPage()
                }
                animatedComposable(page = Page.HomePage) {
                    HomePage(
                        appTheme = appTheme,
                        switchToNextTheme = switchToNextTheme,
                        homeTabSelected = homeTabSelected,
                        onHomeTabSelected = {
                            homeTabSelected = it
                        }
                    )
                }
                animatedComposable(page = Page.FriendProfilePage) { backStackEntry ->
                    FriendProfilePage(
                        friendId = Page.FriendProfilePage.getArgument(backStackEntry)
                    )
                }
                animatedComposable(page = Page.ChatPage) { backStackEntry ->
                    val listState = rememberLazyListState()
                    ChatPage(
                        listState = listState,
                        chat = Page.ChatPage.getArgument(backStackEntry)
                    )
                }
                animatedComposable(page = Page.GroupProfilePage) { backStackEntry ->
                    GroupProfilePage(
                        groupId = Page.GroupProfilePage.getArgument(backStackEntry)
                    )
                }
                animatedComposable(page = Page.UpdateProfilePage) {
                    UpdateProfilePage()
                }
                animatedComposable(page = Page.PreviewImagePage) { backStackEntry ->
                    PreviewImagePage(
                        imagePath = Page.PreviewImagePage.getArgument(
                            backStackEntry
                        )
                    )
                }
            }
        }
    }

    private fun NavGraphBuilder.animatedComposable(
        page: Page,
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route = page.route
        ) { backStackEntry ->
            content(backStackEntry)
        }
    }

}