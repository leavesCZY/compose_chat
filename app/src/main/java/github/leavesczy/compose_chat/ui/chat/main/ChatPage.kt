package github.leavesczy.compose_chat.ui.chat.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatViewModel
import github.leavesczy.compose_chat.ui.theme.AppTheme

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Composable
internal fun ChatPage(chatViewModel: ChatViewModel) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = AppTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        topBar = {
            ChatPageTopBar(
                modifier = Modifier,
                chat = chatViewModel.chatPageViewState.chat,
                title = chatViewModel.chatPageViewState.topBarTitle
            )
        },
        bottomBar = {
            ChatPageBottomBar(
                modifier = Modifier,
                bottomBarViewState = chatViewModel.bottomBarViewState
            )
        }
    ) { innerPadding ->
        val pullRefreshState = rememberPullToRefreshState()
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
                .pullToRefresh(
                    state = pullRefreshState,
                    enabled = !chatViewModel.loadMessageViewState.loadFinish,
                    isRefreshing = chatViewModel.loadMessageViewState.refreshing,
                    onRefresh = chatViewModel.loadMessageViewState.loadMoreMessage
                )
        ) {
            MessagePanel(pageViewState = chatViewModel.chatPageViewState)
            PullToRefreshDefaults.Indicator(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter),
                isRefreshing = chatViewModel.loadMessageViewState.refreshing,
                state = pullRefreshState,
                color = AppTheme.colorScheme.c_FF5386E5_FF5386E5.color,
                containerColor = AppTheme.colorScheme.c_FFFFFFFF_FF45464F.color
            )
        }
    }
}