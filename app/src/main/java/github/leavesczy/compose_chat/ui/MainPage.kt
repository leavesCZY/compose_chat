package github.leavesczy.compose_chat.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.ui.conversation.ConversationPage
import github.leavesczy.compose_chat.ui.friendship.FriendshipDialog
import github.leavesczy.compose_chat.ui.friendship.FriendshipPage
import github.leavesczy.compose_chat.ui.logic.MainPageTab
import github.leavesczy.compose_chat.ui.logic.MainViewModel
import github.leavesczy.compose_chat.ui.person.PersonProfilePage
import github.leavesczy.compose_chat.ui.theme.WindowInsetsEmpty
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPage(mainViewModel: MainViewModel) {
    ModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerState = mainViewModel.drawerViewState.drawerState,
        drawerContent = {
            MainPageDrawer(viewState = mainViewModel.drawerViewState)
        },
        content = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsetsEmpty,
                topBar = {
                    if (mainViewModel.bottomBarViewState.selectedTab != MainPageTab.Person) {
                        MainPageTopBar(viewState = mainViewModel.topBarViewState)
                    }
                },
                bottomBar = {
                    MainPageBottomBar(viewState = mainViewModel.bottomBarViewState)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                ) {
                    when (mainViewModel.bottomBarViewState.selectedTab) {
                        MainPageTab.Conversation -> {
                            ConversationPage()
                        }

                        MainPageTab.Friendship -> {
                            FriendshipPage()
                        }

                        MainPageTab.Person -> {
                            PersonProfilePage()
                        }
                    }
                }
            }
            FriendshipDialog(viewState = mainViewModel.friendshipDialogViewState)
        }
    )
    LoadingDialog(visible = mainViewModel.loadingDialogVisible)
}