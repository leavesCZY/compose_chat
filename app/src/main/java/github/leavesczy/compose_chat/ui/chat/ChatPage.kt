package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.ui.chat.logic.ChatPageAction
import github.leavesczy.compose_chat.ui.chat.logic.ChatViewModel

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ChatPage(chatViewModel: ChatViewModel, chatPageAction: ChatPageAction) {
    val chatPageViewState = chatViewModel.chatPageViewState
    val loadMessageViewState = chatViewModel.loadMessageViewState
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatPageTopBar(
                title = chatPageViewState.topBarTitle,
                chat = chatPageViewState.chat
            )
        },
        bottomBar = {
            ChatPageBottomBar(
                sendTextMessage = {
                    chatViewModel.sendTextMessage(text = it.text)
                },
                sendImageMessage = {
                    chatViewModel.sendImageMessage(mediaResource = it)
                }
            )
        }
    ) { innerPadding ->
        val pullRefreshState =
            rememberPullRefreshState(refreshing = loadMessageViewState.refreshing, onRefresh = {
                chatViewModel.loadMoreMessage()
            })
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .pullRefresh(state = pullRefreshState, enabled = !loadMessageViewState.loadFinish)
        ) {
            MessagePanel(
                chatPageViewState = chatPageViewState,
                chatPageAction = chatPageAction
            )
            PullRefreshIndicator(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                refreshing = loadMessageViewState.refreshing,
                state = pullRefreshState,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}