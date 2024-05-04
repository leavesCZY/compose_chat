package github.leavesczy.compose_chat.ui.conversation.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ConversationType
import github.leavesczy.compose_chat.base.provider.IConversationProvider
import github.leavesczy.compose_chat.proxy.logic.ConversationProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ConversationViewModel : BaseViewModel() {

    private val conversationProvider: IConversationProvider = ConversationProvider()

    val pageViewState by mutableStateOf(
        value = ConversationPageViewState(
            listState = mutableStateOf(
                value = LazyListState(
                    firstVisibleItemIndex = 0,
                    firstVisibleItemScrollOffset = 0
                )
            ),
            conversationList = mutableStateOf(value = emptyList()),
            onClickConversation = ::onClickConversation,
            deleteConversation = ::deleteConversation,
            pinConversation = ::pinConversation
        )
    )

    init {
        viewModelScope.launch {
            conversationProvider.conversationList.collect {
                pageViewState.conversationList.value = it
            }
        }
        conversationProvider.refreshConversationList()
    }

    private fun onClickConversation(conversation: Conversation) {
        when (conversation.type) {
            ConversationType.C2C -> {
                ChatActivity.navTo(
                    context = context,
                    chat = Chat.PrivateChat(id = conversation.id)
                )
            }

            ConversationType.Group -> {
                ChatActivity.navTo(
                    context = context,
                    chat = Chat.GroupChat(id = conversation.id)
                )
            }
        }
    }

    private fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
            val result = when (conversation.type) {
                ConversationType.C2C -> {
                    conversationProvider.deleteC2CConversation(userId = conversation.id)
                }

                ConversationType.Group -> {
                    conversationProvider.deleteGroupConversation(groupId = conversation.id)
                }
            }
            when (result) {
                is ActionResult.Success -> {
                    conversationProvider.refreshConversationList()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

    private fun pinConversation(conversation: Conversation, pin: Boolean) {
        viewModelScope.launch {
            conversationProvider.pinConversation(
                conversation = conversation,
                pin = pin
            )
        }
    }

}