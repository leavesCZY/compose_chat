package github.leavesczy.compose_chat.ui.chat.main.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Message
import kotlinx.collections.immutable.PersistentList

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class ChatPageViewState(
    val chat: Chat,
    val listState: LazyListState,
    val topBarTitle: String,
    val messageList: PersistentList<Message>
)

@Stable
data class LoadMessageViewState(
    val refreshing: Boolean,
    val loadFinish: Boolean
)

@Stable
data class ChatPageAction(
    val onClickAvatar: (Message) -> Unit,
    val onClickMessage: (Message) -> Unit,
    val onLongClickMessage: (Message) -> Unit
)