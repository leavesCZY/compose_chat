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
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.C2CConversation
import github.leavesczy.compose_chat.common.model.GroupConversation
import github.leavesczy.compose_chat.extend.LocalNavHostController
import github.leavesczy.compose_chat.extend.navToGroupChatPage
import github.leavesczy.compose_chat.extend.navToPrivateChatPage
import github.leavesczy.compose_chat.logic.HomeViewModel
import github.leavesczy.compose_chat.model.*
import github.leavesczy.compose_chat.ui.conversation.ConversationPage
import github.leavesczy.compose_chat.ui.friend.FriendshipPage
import github.leavesczy.compose_chat.ui.profile.PersonProfilePage
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
fun HomePage(
    appTheme: AppTheme,
    switchToNextTheme: () -> Unit,
    homeTabSelected: HomePageTab,
    onHomeTabSelected: (HomePageTab) -> Unit
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
    val conversationPageState = remember(key1 = conversationList) {
        ConversationPageState(
            listState = conversationListState,
            conversationList = conversationList,
            onClickConversation = {
                when (it) {
                    is C2CConversation -> {
                        navHostController.navToPrivateChatPage(friendId = it.id)
                    }
                    is GroupConversation -> {
                        navHostController.navToGroupChatPage(groupId = it.id)
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

    val friendshipListState = rememberLazyListState()
    val friendshipPageState = remember(key1 = joinedGroupList, key2 = fiendList) {
        FriendshipPageState(
            listState = friendshipListState,
            joinedGroupList = joinedGroupList,
            friendList = fiendList,
            onClickGroup = {
                navHostController.navToGroupChatPage(groupId = it.id)
            },
            onClickFriend = {
                navHostController.navigate(
                    route = Page.FriendProfilePage.generateRoute(friendId = it.id)
                )
            },
        )
    }
    val personProfilePageState = remember(key1 = personProfile) {
        PersonProfilePageState(personProfile = personProfile)
    }

    val homeDrawerViewState =
        remember(key1 = personProfile, key2 = appTheme, key3 = switchToNextTheme) {
            HomePageDrawerState(
                drawerState = drawerState,
                appTheme = appTheme,
                userProfile = personProfile,
                switchToNextTheme = switchToNextTheme,
                logout = {
                    homeViewModel.logout()
                },
            )
        }

    val homePageTopBarState = remember(
        key1 = homeTabSelected
    ) {
        HomePageTopBarState(
            pageSelected = homeTabSelected,
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

    val homePageBottomBarState = remember(
        key1 = homeTabSelected,
        key2 = totalUnreadCount,
        key3 = onHomeTabSelected
    ) {
        HomePageBottomBarState(
            tabList = HomePageTab.values().toList(),
            tabSelected = homeTabSelected,
            unreadMessageCount = totalUnreadCount,
            onTabSelected = onHomeTabSelected
        )
    }

    val homePageFriendshipPanelState = remember {
        HomePageFriendshipPanelState(
            modalBottomSheetState = sheetState,
            addFriend = {
                homeViewModel.addFriend(userId = it)
            },
            joinGroup = {
                coroutineScope.launch {
                    when (val result = homeViewModel.joinGroup(groupId = it)) {
                        is ActionResult.Success -> {
                            showToast("加入成功")
                            homeViewModel.getJoinedGroupList()
                            sheetState.hide()
                            navHostController.navToGroupChatPage(groupId = it)
                        }
                        is ActionResult.Failed -> {
                            showToast(result.reason)
                        }
                    }
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerShape = RoundedCornerShape(0.dp),
        drawerContent = {
            HomePageDrawer(
                homePageDrawerState = homeDrawerViewState
            )
        },
        content = {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                sheetShape = BottomSheetShape,
                sheetContent = {
                    HomePageFriendshipPanel(homePageFriendshipPanelState = homePageFriendshipPanelState)
                }
            ) {
                Scaffold(
                    topBar = {
                        HomePageTopBar(homePageTopBarState = homePageTopBarState)
                    },
                    bottomBar = {
                        HomePageBottomBar(
                            homePageBottomBarState = homePageBottomBarState
                        )
                    },
                    floatingActionButton = {
                        if (homeTabSelected == HomePageTab.Friendship) {
                            FloatingActionButton(
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
                        HomePageTab.Conversation -> {
                            ConversationPage(
                                paddingValues = paddingValues,
                                conversationPageState = conversationPageState
                            )
                        }
                        HomePageTab.Friendship -> {
                            FriendshipPage(
                                paddingValues = paddingValues,
                                friendshipPageState = friendshipPageState
                            )
                        }
                        HomePageTab.Person -> {
                            PersonProfilePage(
                                personProfilePageState = personProfilePageState
                            )
                        }
                    }
                }
            }
        }
    )
}