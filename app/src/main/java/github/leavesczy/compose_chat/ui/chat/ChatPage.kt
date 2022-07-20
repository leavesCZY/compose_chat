package github.leavesczy.compose_chat.ui.chat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import github.leavesczy.compose_chat.model.ChatPageAction
import github.leavesczy.compose_chat.model.ChatPageViewState
import kotlinx.coroutines.flow.filter

/**
 * @Author: leavesCZY
 * @Date: 2021/7/3 23:53
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ChatPage(
    chatPageViewState: ChatPageViewState,
    chatPageAction: ChatPageAction,
) {
    LaunchedEffect(key1 = "loadMore") {
        snapshotFlow {
            chatPageViewState.listState.firstVisibleItemIndex
        }.filter {
            !chatPageViewState.loadFinish
        }.collect {
            chatPageAction.loadMoreMessage()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatPageTopBar(
                title = chatPageViewState.topBarTitle,
                onClickBackMenu = chatPageAction.onClickBackMenu,
                onClickMoreMenu = chatPageAction.onClickMoreMenu
            )
        },
        bottomBar = {
            ChatPageBottomBar(
                sendText = {
                    chatPageAction.sendTextMessage(it.text)
                }, sendImage = {
                    chatPageAction.sendImageMessage(it)
                })
        }
    ) { innerPadding ->
        MessagePanel(
            contentPadding = innerPadding,
            chatPageViewState = chatPageViewState,
            chatPageAction = chatPageAction
        )
    }
}