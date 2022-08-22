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
    viewState: ChatPageViewState,
    action: ChatPageAction,
) {
    LaunchedEffect(key1 = "loadMore") {
        snapshotFlow {
            viewState.listState.firstVisibleItemIndex
        }.filter {
            !viewState.loadFinish
        }.collect {
            action.loadMoreMessage()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatPageTopBar(
                title = viewState.topBarTitle,
                onClickBackMenu = action.onClickBackMenu,
                onClickMoreMenu = action.onClickMoreMenu
            )
        },
        bottomBar = {
            ChatPageBottomBar(
                sendText = {
                    action.sendTextMessage(it.text)
                }, sendImage = {
                    action.sendImageMessage(it)
                })
        }
    ) { innerPadding ->
        MessagePanel(
            contentPadding = innerPadding,
            chatPageViewState = viewState,
            chatPageAction = action
        )
    }
}