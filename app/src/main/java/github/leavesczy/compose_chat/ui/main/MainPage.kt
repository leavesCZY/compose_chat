package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.main.conversation.ConversationPage
import github.leavesczy.compose_chat.ui.main.conversation.logic.ConversationPageViewState
import github.leavesczy.compose_chat.ui.main.friendship.FriendshipDialog
import github.leavesczy.compose_chat.ui.main.friendship.FriendshipPage
import github.leavesczy.compose_chat.ui.main.friendship.logic.FriendshipDialogViewState
import github.leavesczy.compose_chat.ui.main.friendship.logic.FriendshipPageViewState
import github.leavesczy.compose_chat.ui.main.logic.MainPageBottomBarViewState
import github.leavesczy.compose_chat.ui.main.logic.MainPageDrawerViewState
import github.leavesczy.compose_chat.ui.main.logic.MainPageTab
import github.leavesczy.compose_chat.ui.main.logic.MainPageTopBarViewState
import github.leavesczy.compose_chat.ui.main.person.PersonProfilePage
import github.leavesczy.compose_chat.ui.main.person.logic.PersonProfilePageViewState

@Composable
fun MainPage(
    drawerViewState: MainPageDrawerViewState,
    topBarViewState: MainPageTopBarViewState,
    bottomBarViewState: MainPageBottomBarViewState,
    conversationPageViewState: ConversationPageViewState,
    friendshipPageViewState: FriendshipPageViewState,
    friendshipDialogViewState: FriendshipDialogViewState,
    personProfilePageViewState: PersonProfilePageViewState
) {
    ModalNavigationDrawer(
        modifier = Modifier
            .fillMaxSize(),
        drawerState = drawerViewState.drawerState,
        drawerContent = {
            MainPageDrawer(viewState = drawerViewState)
        },
        content = {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                contentWindowInsets = WindowInsets(),
                containerColor = AppTheme.colorScheme.c_FFFFFFFF_FF101010.color,
                topBar = {
                    if (bottomBarViewState.selectedTab != MainPageTab.Person) {
                        MainPageTopBar(viewState = topBarViewState)
                    }
                },
                bottomBar = {
                    MainPageBottomBar(viewState = bottomBarViewState)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues = innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (bottomBarViewState.selectedTab) {
                        MainPageTab.Conversation -> {
                            ConversationPage(pageViewState = conversationPageViewState)
                        }

                        MainPageTab.Friendship -> {
                            FriendshipPage(pageViewState = friendshipPageViewState)
                        }

                        MainPageTab.Person -> {
                            PersonProfilePage(pageViewState = personProfilePageViewState)
                        }
                    }
                }
            }
            FriendshipDialog(viewState = friendshipDialogViewState)
        }
    )
}