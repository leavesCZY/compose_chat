package github.leavesczy.compose_chat.ui.main.conversation.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ServerConnectState
import kotlinx.collections.immutable.PersistentList

@Stable
data class ConversationPageViewState(
    val listState: LazyListState,
    val serverConnectState: ServerConnectState,
    val conversationList: PersistentList<Conversation>,
    val onClickConversation: (conversation: Conversation) -> Unit,
    val onDeleteConversation: (conversation: Conversation) -> Unit,
    val onPinConversation: (conversation: Conversation, pin: Boolean) -> Unit
)