package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.main.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.main.logic.FriendshipViewModel
import github.leavesczy.compose_chat.ui.main.logic.MainTab
import github.leavesczy.compose_chat.ui.main.logic.MainViewModel
import github.leavesczy.compose_chat.ui.main.logic.PersonProfileViewModel

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPage(
    mainViewModel: MainViewModel,
    conversationViewModel: ConversationViewModel,
    friendshipViewModel: FriendshipViewModel,
    personProfileViewModel: PersonProfileViewModel
) {
    ModalNavigationDrawer(modifier = Modifier.fillMaxSize(),
        drawerState = mainViewModel.drawerViewState.drawerState,
        drawerContent = {
            MainPageDrawer(mainViewModel = mainViewModel)
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.background
                    )
            ) {
                val bottomBarViewState = mainViewModel.bottomBarViewState
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1f)
                    ) {
                        when (bottomBarViewState.selectedTab) {
                            MainTab.Conversation -> {
                                MainPageTopBar(mainViewModel = mainViewModel)
                                ConversationPage(conversationViewModel = conversationViewModel)
                            }

                            MainTab.Friendship -> {
                                MainPageTopBar(mainViewModel = mainViewModel)
                                FriendshipPage(
                                    mainViewModel = mainViewModel,
                                    friendshipViewModel = friendshipViewModel
                                )
                            }

                            MainTab.Person -> {
                                PersonProfilePage(personProfileViewModel = personProfileViewModel)
                            }
                        }
                    }
                    key(AppThemeProvider.appTheme) {
                        MainPageBottomBar(
                            mainViewModel = mainViewModel, viewState = bottomBarViewState
                        )
                    }
                }
                FriendshipDialog(viewState = mainViewModel.friendshipDialogViewState)
            }
        })
}