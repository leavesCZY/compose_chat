package github.leavesczy.compose_chat.ui.chat.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatPageBottomBarViewState
import github.leavesczy.compose_chat.ui.chat.main.logic.ChatPageViewState
import github.leavesczy.compose_chat.ui.chat.main.logic.LoadMessageViewState

@Composable
internal fun ChatPage(
    pageViewState: ChatPageViewState,
    bottomBarViewState: ChatPageBottomBarViewState,
    loadMessageViewState: LoadMessageViewState
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = AppTheme.colorScheme.c_FFEAF2FF_FF0B1220.color,
        topBar = {
            ChatPageTopBar(
                modifier = Modifier,
                pageViewState = pageViewState
            )
        },
        bottomBar = {
            ChatPageBottomBar(
                modifier = Modifier,
                bottomBarViewState = bottomBarViewState
            )
        }
    ) { innerPadding ->
        ChatPageBody(
            innerPadding = innerPadding,
            pageViewState = pageViewState,
            loadMessageViewState = loadMessageViewState
        )
    }
}

@Composable
private fun ChatPageBody(
    innerPadding: PaddingValues,
    pageViewState: ChatPageViewState,
    loadMessageViewState: LoadMessageViewState
) {
    val pullRefreshState = rememberPullToRefreshState()
    Box(
        modifier = Modifier
            .padding(paddingValues = innerPadding)
            .fillMaxSize()
            .pullToRefresh(
                state = pullRefreshState,
                enabled = !loadMessageViewState.isLoadFinished,
                isRefreshing = loadMessageViewState.isRefreshing,
                onRefresh = loadMessageViewState.onLoadMoreMessage
            )
    ) {
        MessagePanel(pageViewState = pageViewState)
        PullToRefreshDefaults.Indicator(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter),
            isRefreshing = loadMessageViewState.isRefreshing,
            state = pullRefreshState,
            color = AppTheme.colorScheme.c_FF5BA3F7_FF60A5FA.color,
            containerColor = AppTheme.colorScheme.c_FFFFFFFF_FF243044.color
        )
    }
}