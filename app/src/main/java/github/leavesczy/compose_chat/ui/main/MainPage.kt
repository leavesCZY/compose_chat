package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.model.*
import github.leavesczy.compose_chat.ui.theme.WindowInsetsEmpty

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
    friendshipPanelViewState: FriendshipPanelViewState,
    bottomBarViewState: MainPageBottomBarViewState,
    drawerViewState: MainPageDrawerViewState
) {
    ModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerState = drawerViewState.drawerState,
        drawerContent = {
            MainPageDrawer(
                viewState = drawerViewState,
                mainPageAction = mainPageAction
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsetsEmpty,
                    topBar = {
                        MainPageTopBar(
                            tabSelected = bottomBarViewState.tabSelected,
                            mainPageAction = mainPageAction
                        )
                    },
                    bottomBar = {
                        MainPageBottomBar(
                            viewState = bottomBarViewState,
                            mainPageAction = mainPageAction
                        )
                    },
                    floatingActionButton = {
                        if (bottomBarViewState.tabSelected == MainTab.Friendship) {
                            FloatingActionButton(
                                modifier = Modifier.padding(bottom = 20.dp),
                                containerColor = MaterialTheme.colorScheme.primary,
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        tint = Color.White,
                                        contentDescription = null,
                                    )
                                },
                                onClick = {
                                    mainPageAction.showFriendshipPanel()
                                })
                        }
                    },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding)
                    ) {
                        when (bottomBarViewState.tabSelected) {
                            MainTab.Conversation -> {
                                ConversationPage(
                                    viewState = conversationPageViewState,
                                    mainPageAction = mainPageAction
                                )
                            }
                            MainTab.Friendship -> {
                                FriendshipPage(
                                    viewState = friendshipPageViewState,
                                    mainPageAction = mainPageAction
                                )
                            }
                            MainTab.Person -> {
                                PersonProfilePage(viewState = personProfilePageViewState)
                            }
                        }
                    }
                }
                FriendshipPanel(viewState = friendshipPanelViewState)
            }
        }
    )
}