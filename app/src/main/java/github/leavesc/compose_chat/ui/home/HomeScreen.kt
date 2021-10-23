package github.leavesc.compose_chat.ui.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.imePadding
import com.google.accompanist.insets.navigationBarsPadding
import github.leavesc.compose_chat.base.model.ServerState
import github.leavesc.compose_chat.cache.AccountInfoCache
import github.leavesc.compose_chat.extend.navigate
import github.leavesc.compose_chat.extend.navigateWithBack
import github.leavesc.compose_chat.logic.HomeViewModel
import github.leavesc.compose_chat.model.AppTheme
import github.leavesc.compose_chat.model.HomeDrawerViewState
import github.leavesc.compose_chat.model.HomeScreenTab
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.theme.BottomSheetShape
import github.leavesc.compose_chat.utils.log
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/4 1:04
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun HomeScreen(
    navController: NavHostController,
    appTheme: AppTheme,
    switchToNextTheme: () -> Unit,
    screenSelected: HomeScreenTab,
    onTabSelected: (HomeScreenTab) -> Unit
) {
    val homeViewModel = viewModel<HomeViewModel>()
    log(log = "homeViewModel: $homeViewModel")
    LaunchedEffect(Unit) {
        launch {
            homeViewModel.serverConnectState.collect {
                when (it) {
                    ServerState.KickedOffline -> {
                        showToast("本账号已在其它客户端登陆，请重新登陆")
                        AccountInfoCache.onUserLogout()
                        navController.navigateWithBack(
                            screen = Screen.LoginScreen
                        )
                    }
                    ServerState.Logout -> {
                        navController.navigateWithBack(
                            screen = Screen.LoginScreen
                        )
                    }
                    else -> {
                        showToast("Connect State Changed : $it")
                    }
                }
            }
        }
        homeViewModel.init()
    }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)
    val sheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val conversationList by homeViewModel.conversationList.collectAsState()
    val totalUnreadCount by homeViewModel.totalUnreadCount.collectAsState()
    val friendList by homeViewModel.fiendList.collectAsState()
    val userProfile by homeViewModel.personProfile.collectAsState()

    val conversationListState = rememberLazyListState()
    val friendShipListState = rememberLazyListState()

    fun sheetContentAnimateTo(targetValue: ModalBottomSheetValue) {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = targetValue)
        }
    }

    ModalBottomSheetLayout(
        modifier = Modifier.navigationBarsPadding(),
        sheetState = sheetState,
        sheetShape = BottomSheetShape,
        sheetContent = {
            HomeMoreActionScreen(
                modalBottomSheetState = sheetState,
                onAddFriend = {
                    homeViewModel.addFriend(userId = it)
                })
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(it) { data ->
                    Snackbar(
                        modifier = Modifier.imePadding(),
                        backgroundColor = MaterialTheme.colors.primary,
                        elevation = 0.dp,
                        snackbarData = data,
                    )
                }
            },
            topBar = {
                HomeScreenTopBar(
                    screenSelected = screenSelected,
                    openDrawer = {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                    onAddFriend = {
                        sheetContentAnimateTo(targetValue = ModalBottomSheetValue.Expanded)
                    },
                    onCreateGroup = {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(message = "功能尚未开发")
                        }
                    }
                )
            },
            bottomBar = {
                HomeScreenBottomBar(
                    screenList = HomeScreenTab.values().toList(),
                    screenSelected = screenSelected,
                    unreadMessageCount = totalUnreadCount,
                    onTabSelected = onTabSelected
                )
            },
            drawerContent = {
                HomeDrawerScreen(
                    drawerState = drawerState,
                    homeDrawerViewState = HomeDrawerViewState(
                        appTheme = appTheme,
                        userProfile = userProfile,
                        switchToNextTheme = switchToNextTheme,
                        updateProfile = { faceUrl: String, nickname: String, signature: String ->
                            homeViewModel.updateProfile(
                                faceUrl = faceUrl,
                                nickname = nickname,
                                signature = signature
                            )
                        },
                        logout = {
                            homeViewModel.logout()
                        },
                    ),
                )
            },
            floatingActionButton = {
                if (screenSelected == HomeScreenTab.Friendship) {
                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colors.primary,
                        onClick = {
                            sheetContentAnimateTo(targetValue = ModalBottomSheetValue.Expanded)
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            tint = Color.White,
                            contentDescription = null,
                        )
                    }
                }
            },
        ) { paddingValues ->
            when (screenSelected) {
                HomeScreenTab.Conversation -> {
                    ConversationScreen(
                        listState = conversationListState,
                        paddingValues = paddingValues,
                        conversationList = conversationList,
                        onClickConversation = {
                            navController.navigate(screen = Screen.ChatScreen(friendId = it.id))
                        },
                        onDeleteConversation = {
                            homeViewModel.deleteConversation(it)
                        },
                        onConversationPinnedChanged = { conversation, pin ->
                            homeViewModel.pinConversation(
                                conversation = conversation,
                                pin = pin
                            )
                        }
                    )
                }
                HomeScreenTab.Friendship -> {
                    FriendshipScreen(
                        listState = friendShipListState,
                        paddingValues = paddingValues,
                        friendList = friendList,
                        onClickFriend = {
                            navController.navigate(
                                screen = Screen.FriendProfileScreen(friendId = it.userId)
                            )
                        },
                    )
                }
                HomeScreenTab.UserProfile -> {
                    UserProfileScreen(
                        userProfile = userProfile
                    )
                }
            }
        }
    }
}