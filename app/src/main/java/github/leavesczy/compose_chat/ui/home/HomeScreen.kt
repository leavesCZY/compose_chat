package github.leavesczy.compose_chat.ui.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.C2CConversation
import github.leavesczy.compose_chat.base.model.GroupConversation
import github.leavesczy.compose_chat.extend.LocalNavHostController
import github.leavesczy.compose_chat.extend.navToC2CChatScreen
import github.leavesczy.compose_chat.extend.navToGroupChatScreen
import github.leavesczy.compose_chat.logic.HomeViewModel
import github.leavesczy.compose_chat.model.*
import github.leavesczy.compose_chat.ui.conversation.ConversationScreen
import github.leavesczy.compose_chat.ui.friend.FriendshipScreen
import github.leavesczy.compose_chat.ui.person.PersonProfileScreen
import github.leavesczy.compose_chat.ui.theme.BottomSheetShape
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/7/4 1:04
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun HomeScreen(
    appTheme: AppTheme,
    switchToNextTheme: () -> Unit,
    homeTabSelected: HomeScreenTab,
    onHomeTabSelected: (HomeScreenTab) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val navHostController = LocalNavHostController.current

    fun sheetContentAnimateToExpanded() {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = ModalBottomSheetValue.Expanded)
        }
    }

    val homeViewModel = viewModel<HomeViewModel>()

    val conversationList by homeViewModel.conversationList.collectAsState()
    val totalUnreadCount by homeViewModel.totalUnreadCount.collectAsState()
    val fiendList by homeViewModel.fiendList.collectAsState()
    val joinedGroupList by homeViewModel.joinedGroupList.collectAsState()
    val personProfile by homeViewModel.personProfile.collectAsState()

    val conversationListState = rememberLazyListState()
    val conversationScreenState = remember(key1 = conversationList) {
        ConversationScreenState(
            listState = conversationListState,
            conversationList = conversationList,
            onClickConversation = {
                when (it) {
                    is C2CConversation -> {
                        navHostController.navToC2CChatScreen(friendId = it.id)
                    }
                    is GroupConversation -> {
                        navHostController.navToGroupChatScreen(groupId = it.id)
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
            joinedGroupList = joinedGroupList,
            friendList = fiendList,
            onClickGroup = {
                navHostController.navToGroupChatScreen(groupId = it.id)
            },
            onClickFriend = {
                navHostController.navigate(
                    route = Screen.FriendProfileScreen.generateRoute(friendId = it.userId)
                )
            },
        )
    }
    val personProfileScreenState = remember(key1 = personProfile) {
        PersonProfileScreenState(personProfile = personProfile)
    }

    val homeDrawerViewState =
        remember(key1 = personProfile, key2 = appTheme, key3 = switchToNextTheme) {
            HomeScreenDrawerState(
                drawerState = drawerState,
                appTheme = appTheme,
                userProfile = personProfile,
                switchToNextTheme = switchToNextTheme,
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
                    drawerState.open()
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
        key2 = totalUnreadCount,
        key3 = onHomeTabSelected
    ) {
        HomeScreenBottomBarState(
            tabList = HomeScreenTab.values().toList(),
            tabSelected = homeTabSelected,
            unreadMessageCount = totalUnreadCount,
            onTabSelected = onHomeTabSelected
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
                            navHostController.navToGroupChatScreen(groupId = it)
                        }
                        is ActionResult.Failed -> {
                            if (result.code == 10013) {
                                sheetState.hide()
                                navHostController.navToGroupChatScreen(groupId = it)
                            } else {
                                showToast(result.reason)
                            }
                        }
                    }
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeScreenDrawer(
                homeScreenDrawerState = homeDrawerViewState
            )
        },
        drawerShape = RoundedCornerShape(0.dp),
        content = {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                sheetShape = BottomSheetShape,
                sheetContent = {
                    HomeScreenSheetContent(homeScreenSheetContentState = homeSheetContentState)
                }
            ) {
                Scaffold(
                    topBar = {
                        HomeScreenTopBar(homeScreenTopBarState = homeScreenTopBarState)
                    },
                    bottomBar = {
                        HomeScreenBottomBar(
                            homeScreenBottomBarState = homeScreenBottomBarState
                        )
                    },
                    floatingActionButton = {
                        if (homeTabSelected == HomeScreenTab.Friendship) {
                            SmallFloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.primary,
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        tint = Color.White,
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    sheetContentAnimateToExpanded()
                                })
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
    )
}