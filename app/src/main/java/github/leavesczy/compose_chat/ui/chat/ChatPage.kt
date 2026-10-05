package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageBottomBarViewState
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageViewState
import github.leavesczy.compose_chat.ui.chat.logic.LoadMessageViewState

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
        val loadError = loadMessageViewState.loadError
        if (!loadError.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(color = AppTheme.colorScheme.c_6684A9D6_6684A9D6.color)
                    .clickable(onClick = loadMessageViewState.onRetryLoad)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(weight = 1f),
                    text = stringResource(id = R.string.chat_history_load_failed, loadError),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = AppTheme.colorScheme.c_FF3D5A80_99FFFFFF.color
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    text = stringResource(id = R.string.retry),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = AppTheme.colorScheme.c_FF5BA3F7_FF60A5FA.color
                )
            }
        }
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
