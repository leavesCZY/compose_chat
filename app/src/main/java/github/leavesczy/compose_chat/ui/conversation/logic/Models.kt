package github.leavesczy.compose_chat.ui.conversation.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ServerConnectState
import kotlinx.collections.immutable.PersistentList

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Stable
data class ConversationPageViewState(
    val listState: LazyListState,
    val serverConnectState: ServerConnectState,
    val conversationList: PersistentList<Conversation>,
    val onClickConversation: (conversation: Conversation) -> Unit,
    val deleteConversation: (conversation: Conversation) -> Unit,
    val pinConversation: (conversation: Conversation, pin: Boolean) -> Unit
)