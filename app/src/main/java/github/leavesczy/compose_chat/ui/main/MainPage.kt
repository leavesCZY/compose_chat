package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.model.*
import github.leavesczy.compose_chat.ui.theme.BottomSheetShape
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/7/4 1:04
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPage(
    mainPageAction: MainPageAction,
    conversationPageViewState: ConversationPageViewState,
    friendshipPageViewState: FriendshipPageViewState,
    personProfilePageViewState: PersonProfilePageViewState,
    mainPageBottomBarViewState: MainPageBottomBarViewState,
    mainPageDrawerViewState: MainPageDrawerViewState
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val tabSelected = mainPageBottomBarViewState.tabSelected
    fun sheetContentAnimateToExpanded() {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = ModalBottomSheetValue.Expanded)
        }
    }

    val topBarViewState = remember(key1 = tabSelected) {
        MainPageTopBarViewState(
            tabSelected = tabSelected,
            openDrawer = {
                mainPageAction.changDrawerState(DrawerValue.Open)
            },
            onAddFriend = {
                sheetContentAnimateToExpanded()
            },
            onJoinGroup = {
                sheetContentAnimateToExpanded()
            }
        )
    }
    ModalNavigationDrawer(
        drawerState = mainPageDrawerViewState.drawerState,
        drawerShape = RoundedCornerShape(size = 0.dp),
        drawerContent = {
            MainPageDrawer(
                mainPageDrawerViewState = mainPageDrawerViewState,
                mainPageAction = mainPageAction
            )
        },
        content = {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                sheetShape = BottomSheetShape,
                sheetContent = {
                    MainPageFriendshipPanel(
                        modalBottomSheetState = sheetState,
                        mainPageAction = mainPageAction
                    )
                }
            ) {
                Scaffold(
                    modifier = Modifier.navigationBarsPadding(),
                    topBar = {
                        MainPageTopBar(mainPageTopBarState = topBarViewState)
                    },
                    bottomBar = {
                        MainPageBottomBar(
                            mainPageBottomBarViewState = mainPageBottomBarViewState,
                            mainPageAction = mainPageAction
                        )
                    },
                    floatingActionButton = {
                        if (tabSelected == MainTab.Friendship) {
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
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding)
                    ) {
                        when (tabSelected) {
                            MainTab.Conversation -> {
                                ConversationPage(
                                    conversationPageViewState = conversationPageViewState,
                                    mainPageAction = mainPageAction
                                )
                            }
                            MainTab.Friendship -> {
                                FriendshipPage(
                                    friendshipPageViewState = friendshipPageViewState,
                                    mainPageAction = mainPageAction
                                )
                            }
                            MainTab.Person -> {
                                PersonProfilePage(
                                    personProfilePageViewState = personProfilePageViewState
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}