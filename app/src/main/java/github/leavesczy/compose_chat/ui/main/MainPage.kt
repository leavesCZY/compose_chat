package github.leavesczy.compose_chat.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.main.conversation.ConversationPage
import github.leavesczy.compose_chat.ui.main.conversation.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.main.friendship.FriendshipDialog
import github.leavesczy.compose_chat.ui.main.friendship.FriendshipPage
import github.leavesczy.compose_chat.ui.main.friendship.logic.FriendshipViewModel
import github.leavesczy.compose_chat.ui.main.logic.MainPageBottomBarViewState
import github.leavesczy.compose_chat.ui.main.logic.MainPageDrawerViewState
import github.leavesczy.compose_chat.ui.main.logic.MainPageTab
import github.leavesczy.compose_chat.ui.main.logic.MainPageTopBarViewState
import github.leavesczy.compose_chat.ui.main.person.PersonProfilePage
import github.leavesczy.compose_chat.ui.main.person.logic.PersonProfileViewModel
import github.leavesczy.compose_chat.widgets.LoadingDialog

@Composable
internal fun MainPage(
    drawerViewState: MainPageDrawerViewState,
    topBarViewState: MainPageTopBarViewState,
    bottomBarViewState: MainPageBottomBarViewState
) {
    val selectedTab = bottomBarViewState.selectedTab
    var friendshipViewModelAttached by remember {
        mutableStateOf(value = false)
    }
    var pendingShowFriendshipDialog by remember {
        mutableStateOf(value = false)
    }
    val friendshipPanelVisible = friendshipViewModelAttached ||
            selectedTab == MainPageTab.Friendship ||
            pendingShowFriendshipDialog
    LaunchedEffect(key1 = friendshipPanelVisible) {
        if (friendshipPanelVisible) {
            friendshipViewModelAttached = true
        }
    }
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
                containerColor = AppTheme.colorScheme.c_FFEAF2FF_FF0B1220.color,
                topBar = {
                    if (selectedTab != MainPageTab.Person) {
                        MainPageTopBar(
                            viewState = topBarViewState,
                            onClickShowFriendshipDialog = {
                                pendingShowFriendshipDialog = true
                            }
                        )
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
                    when (selectedTab) {
                        MainPageTab.Conversation -> {
                            ConversationTabRoute()
                        }

                        MainPageTab.Friendship -> {
                            FriendshipTabRoute()
                        }

                        MainPageTab.Person -> {
                            PersonTabRoute()
                        }
                    }
                }
            }
            if (friendshipPanelVisible) {
                FriendshipPanelRoute(
                    pendingShowFriendshipDialog = pendingShowFriendshipDialog,
                    onPendingShowFriendshipDialogConsumed = {
                        pendingShowFriendshipDialog = false
                    }
                )
            }
        }
    )
}

@Composable
private fun ConversationTabRoute() {
    val conversationViewModel = viewModel<ConversationViewModel>()
    ConversationPage(pageViewState = conversationViewModel.pageViewState)
}

@Composable
private fun FriendshipTabRoute() {
    val friendshipViewModel = viewModel<FriendshipViewModel>()
    FriendshipPage(pageViewState = friendshipViewModel.pageViewState)
}

@Composable
private fun PersonTabRoute() {
    val personProfileViewModel = viewModel<PersonProfileViewModel>()
    PersonProfilePage(pageViewState = personProfileViewModel.pageViewState)
}

@Composable
private fun FriendshipPanelRoute(
    pendingShowFriendshipDialog: Boolean,
    onPendingShowFriendshipDialogConsumed: () -> Unit
) {
    val friendshipViewModel = viewModel<FriendshipViewModel>()
    LaunchedEffect(key1 = pendingShowFriendshipDialog) {
        if (pendingShowFriendshipDialog) {
            friendshipViewModel.showFriendshipDialog()
            onPendingShowFriendshipDialogConsumed()
        }
    }
    FriendshipDialog(viewState = friendshipViewModel.friendshipDialogViewState)
    LoadingDialog(viewState = friendshipViewModel.loadingDialogViewState)
}


