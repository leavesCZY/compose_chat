package github.leavesc.compose_chat.ui.home

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsPadding
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.Conversation
import github.leavesc.compose_chat.base.model.ServerState
import github.leavesc.compose_chat.cache.AccountCache
import github.leavesc.compose_chat.extend.navToC2CChatScreen
import github.leavesc.compose_chat.extend.navToGroupChatScreen
import github.leavesc.compose_chat.extend.navigateWithBack
import github.leavesc.compose_chat.logic.HomeViewModel
import github.leavesc.compose_chat.model.AppTheme
import github.leavesc.compose_chat.model.HomeDrawerViewState
import github.leavesc.compose_chat.model.HomeScreenTab
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.conversation.ConversationScreen
import github.leavesc.compose_chat.ui.friend.FriendshipScreen
import github.leavesc.compose_chat.ui.person.PersonProfileScreen
import github.leavesc.compose_chat.ui.theme.BottomSheetShape
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
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
    homeScreenSelected: HomeScreenTab,
    onHomeScreenTabSelected: (HomeScreenTab) -> Unit
) {
    val homeViewModel = viewModel<HomeViewModel>()

    val conversationList by homeViewModel.conversationList.collectAsState()
    val totalUnreadCount by homeViewModel.totalUnreadCount.collectAsState()
    val friendList by homeViewModel.fiendList.collectAsState()
    val joinedGroupList by homeViewModel.joinedGroupList.collectAsState()
    val personProfile by homeViewModel.personProfile.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val conversationListState = rememberSaveable(
        inputs = arrayOf(totalUnreadCount),
        saver = LazyListState.Saver
    ) {
        LazyListState(
            0,
            0
        )
    }
    val friendShipListState = rememberSaveable(
        inputs = arrayOf(joinedGroupList.size + friendList.size),
        saver = LazyListState.Saver
    ) {
        LazyListState(
            0,
            0
        )
    }
    val homeDrawerViewState = remember(key1 = appTheme, key2 = personProfile) {
        HomeDrawerViewState(
            appTheme = appTheme,
            userProfile = personProfile,
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
        )
    }

    fun sheetContentAnimateTo(targetValue: ModalBottomSheetValue) {
        coroutineScope.launch(Dispatchers.Main) {
            sheetState.animateTo(targetValue = targetValue)
        }
    }

    LaunchedEffect(key1 = Unit) {
        launch {
            homeViewModel.serverConnectState.collect {
                when (it) {
                    ServerState.KickedOffline -> {
                        showToast("本账号已在其它客户端登陆，请重新登陆")
                        AccountCache.onUserLogout()
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
    }

    ModalBottomSheetLayout(
        modifier = Modifier.navigationBarsPadding(),
        sheetState = sheetState,
        sheetShape = BottomSheetShape,
        sheetContent = {
            HomeMoreActionScreen(
                modalBottomSheetState = sheetState,
                toAddFriend = {
                    homeViewModel.addFriend(userId = it)
                }, toJoinGroup = {
                    coroutineScope.launch(Dispatchers.Main) {
                        when (val result = homeViewModel.joinGroup(groupId = it)) {
                            is ActionResult.Success -> {
                                showToast("加入成功")
                                sheetState.hide()
                                navController.navToGroupChatScreen(groupId = it)
                            }
                            is ActionResult.Failed -> {
                                if (result.code == 10013) {
                                    sheetState.hide()
                                    navController.navToGroupChatScreen(groupId = it)
                                } else {
                                    showToast(result.reason)
                                }
                            }
                        }
                    }
                })
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                HomeScreenTopBar(
                    screenSelected = homeScreenSelected,
                    openDrawer = {
                        coroutineScope.launch(Dispatchers.Main) {
                            scaffoldState.drawerState.open()
                        }
                    },
                    onAddFriend = {
                        sheetContentAnimateTo(targetValue = ModalBottomSheetValue.Expanded)
                    },
                    onJoinGroup = {
                        sheetContentAnimateTo(targetValue = ModalBottomSheetValue.Expanded)
                    }
                )
            },
            bottomBar = {
                HomeScreenBottomBar(
                    homeScreenList = HomeScreenTab.values().toList(),
                    homeScreenSelected = homeScreenSelected,
                    unreadMessageCount = totalUnreadCount,
                    onHomeScreenTabSelected = onHomeScreenTabSelected
                )
            },
            drawerContent = {
                HomeDrawerScreen(
                    drawerState = drawerState,
                    homeDrawerViewState = homeDrawerViewState
                )
            },
            drawerShape = RoundedCornerShape(0.dp),
            floatingActionButton = {
                if (homeScreenSelected == HomeScreenTab.Friendship) {
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
            when (homeScreenSelected) {
                HomeScreenTab.Conversation -> {
                    ConversationScreen(
                        listState = conversationListState,
                        paddingValues = paddingValues,
                        conversationList = conversationList,
                        onClickConversation = {
                            when (it) {
                                is Conversation.C2CConversation -> {
                                    navController.navToC2CChatScreen(friendId = it.id)
                                }
                                is Conversation.GroupConversation -> {
                                    navController.navToGroupChatScreen(groupId = it.id)
                                }
                            }
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
                        joinedGroupList = joinedGroupList,
                        friendList = friendList,
                        onClickGroup = {
                            navController.navToGroupChatScreen(groupId = it.id)
                        },
                        onClickFriend = {
                            navController.navigate(
                                route = Screen.FriendProfileScreen.generateRoute(friendId = it.userId)
                            )
                        },
                    )
                }
                HomeScreenTab.Person -> {
                    PersonProfileScreen(
                        personProfile = personProfile
                    )
                }
            }
        }
    }
}