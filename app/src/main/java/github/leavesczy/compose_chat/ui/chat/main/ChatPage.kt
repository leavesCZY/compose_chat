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
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatViewModel
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.theme.WindowInsetsEmpty

/**
 * @Author: leavesCZY
 * @Date: 2026/1/23 21:24
 * @Desc:
 */
@Composable
internal fun ChatPage(
    chatViewModel: ChatViewModel,
    chatPageAction: ChatPageAction
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        contentWindowInsets = WindowInsetsEmpty,
        topBar = {
            ChatPageTopBar(
                title = chatViewModel.chatPageViewState.topBarTitle,
                chat = chatViewModel.chatPageViewState.chat
            )
        },
        bottomBar = {
            ChatPageBottomBar(chatViewModel = chatViewModel)
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
                    onRefresh = {
                        chatViewModel.loadMoreMessage()
                    }
                )
        ) {
            MessagePanel(
                pageViewState = chatViewModel.chatPageViewState,
                chatPageAction = chatPageAction
            )
            PullToRefreshDefaults.Indicator(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter),
                isRefreshing = chatViewModel.loadMessageViewState.refreshing,
                state = pullRefreshState,
                color = ComposeChatTheme.colorScheme.c_FF5386E5_FF5386E5.color,
                containerColor = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF45464F.color
            )
        }
    }
}