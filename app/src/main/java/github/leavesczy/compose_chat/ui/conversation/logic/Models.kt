package github.leavesczy.compose_chat.ui.conversation.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.Conversation

@Stable
data class ConversationPageViewState(
    val listState: MutableState<LazyListState>,
    val conversationList: MutableState<List<Conversation>>,
    val onClickConversation: (Conversation) -> Unit,
    val deleteConversation: (Conversation) -> Unit,
    val pinConversation: (Conversation, Boolean) -> Unit
)