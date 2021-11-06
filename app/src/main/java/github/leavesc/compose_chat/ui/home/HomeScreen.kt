package github.leavesc.compose_chat.ui.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsPadding
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.Conversation
import github.leavesc.compose_chat.extend.navToC2CChatScreen
import github.leavesc.compose_chat.extend.navToGroupChatScreen
import github.leavesc.compose_chat.logic.HomeViewModel
import github.leavesc.compose_chat.model.*
import github.leavesc.compose_chat.ui.conversation.ConversationScreen
import github.leavesc.compose_chat.ui.friend.FriendshipScreen
import github.leavesc.compose_chat.ui.person.PersonProfileScreen
import github.leavesc.compose_chat.ui.theme.BottomSheetShape
import github.leavesc.compose_chat.utils.showToast
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
    homeTabSelected: HomeScreenTab,
    onHomeTabSelected: (HomeScreenTab) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    fun sheetContentAnimateToExpanded() {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = ModalBottomSheetValue.Expanded)
        }
    }

    val homeViewModel = viewModel<HomeViewModel>()

    val personProfile by homeViewModel.personProfile.collectAsState()
    val conversationList by homeViewModel.conversationList.collectAsState()
    val fiendList by homeViewModel.fiendList.collectAsState()
    val joinedGroupList by homeViewModel.joinedGroupList.collectAsState()

    val conversationListState = rememberLazyListState()
    val conversationScreenState = remember(key1 = conversationList) {
        ConversationScreenState(
            listState = conversationListState,
            conversationList = homeViewModel.conversationList.value,
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
            onPinnedConversation = { conversation, pin ->
                homeViewModel.pinConversation(
                    conversation = conversation,
                    pin = pin
                )
            }
        )
    }

    val friendShipListState = rememberLazyListState()
    val friendshipScreenState = remember(key1 = joinedGroupList, key2 = fiendList) {
        FriendshipScreenState(
            listState = friendShipListState,
            joinedGroupList = homeViewModel.joinedGroupList.value,
            friendList = homeViewModel.fiendList.value,
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
    val personProfileScreenState = remember(key1 = personProfile) {
        PersonProfileScreenState(personProfile = homeViewModel.personProfile.value)
    }

    val homeDrawerViewState =
        remember(key1 = personProfile, key2 = appTheme, key3 = switchToNextTheme) {
            HomeScreenDrawerState(
                drawerState = drawerState,
                appTheme = appTheme,
                userProfile = homeViewModel.personProfile.value,
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

    val homeScreenTopBarState = remember(
        key1 = homeTabSelected
    ) {
        HomeScreenTopBarState(
            screenSelected = homeTabSelected,
            openDrawer = {
                coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }
            },
            onAddFriend = {
                sheetContentAnimateToExpanded()
            },
            onJoinGroup = {
                sheetContentAnimateToExpanded()
            }
        )
    }

    val homeScreenBottomBarState = remember(
        key1 = homeTabSelected,
        key2 = homeViewModel.totalUnreadCount.collectAsState(),
        key3 = onHomeTabSelected
    ) {
        HomeScreenBottomBarState(
            homeScreenList = HomeScreenTab.values().toList(),
            homeScreenSelected = homeTabSelected,
            unreadMessageCount = homeViewModel.totalUnreadCount.value,
            onHomeScreenTabSelected = onHomeTabSelected
        )
    }

    val homeSheetContentState = remember {
        HomeScreenSheetContentState(
            modalBottomSheetState = sheetState,
            toAddFriend = {
                homeViewModel.addFriend(userId = it)
            },
            toJoinGroup = {
                coroutineScope.launch {
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
            }
        )
    }

    ModalBottomSheetLayout(
        modifier = Modifier.navigationBarsPadding(),
        sheetState = sheetState,
        sheetShape = BottomSheetShape,
        sheetContent = {
            HomeScreenSheetContent(homeScreenSheetContentState = homeSheetContentState)
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                HomeScreenTopBar(homeScreenTopBarState = homeScreenTopBarState)
            },
            bottomBar = {
                HomeScreenBottomBar(
                    homeScreenBottomBarState = homeScreenBottomBarState
                )
            },
            drawerContent = {
                HomeScreenDrawer(
                    homeScreenDrawerState = homeDrawerViewState
                )
            },
            drawerShape = RoundedCornerShape(0.dp),
            floatingActionButton = {
                if (homeTabSelected == HomeScreenTab.Friendship) {
                    FloatingActionButton(
                        backgroundColor = MaterialTheme.colors.primary,
                        onClick = {
                            sheetContentAnimateToExpanded()
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
            when (homeTabSelected) {
                HomeScreenTab.Conversation -> {
                    ConversationScreen(
                        paddingValues = paddingValues,
                        conversationScreenState = conversationScreenState
                    )
                }
                HomeScreenTab.Friendship -> {
                    FriendshipScreen(
                        paddingValues = paddingValues,
                        friendshipScreenState = friendshipScreenState
                    )
                }
                HomeScreenTab.Person -> {
                    PersonProfileScreen(
                        personProfileScreenState = personProfileScreenState
                    )
                }
            }
        }
    }
}