package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesc.compose_chat.base.model.Chat
import github.leavesc.compose_chat.base.model.Message
import github.leavesc.compose_chat.model.ChatScreenState

/**
 * @Author: leavesC
 * @Date: 2021/10/26 15:50
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun MessageScreen(
    listState: LazyListState,
    chat: Chat,
    contentPadding: PaddingValues,
    chatScreenState: ChatScreenState,
    onClickAvatar: (Message) -> Unit,
    onClickMessage: (Message) -> Unit,
    onLongClickMessage: (Message) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        state = listState,
        reverseLayout = true,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.Top,
    ) {
        for (message in chatScreenState.messageList) {
            item(key = message.messageDetail.msgId) {
                MessageItems(
                    message = message,
                    showPartyName = chat is Chat.Group,
                    onClickAvatar = onClickAvatar,
                    onClickMessage = onClickMessage,
                    onLongClickMessage = onLongClickMessage
                )
            }
        }
        if (chatScreenState.showLoadMore) {
            item {
                LoadMoreMessage()
            }
        }
    }
}