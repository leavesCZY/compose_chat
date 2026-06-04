package github.leavesczy.compose_chat.ui.main.conversation.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ConversationType
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.ui.chat.main.ChatActivity
import github.leavesczy.compose_chat.ui.main.friendship.logic.FriendshipViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
abstract class ConversationViewModel : FriendshipViewModel() {

    var conversationPageViewState by mutableStateOf(
        value = ConversationPageViewState(
            listState = LazyListState(),
            serverConnectState = ServerConnectState.Idle,
            conversationList = persistentListOf(),
            onClickConversation = ::onClickConversation,
            onDeleteConversation = ::deleteConversation,
            onPinConversation = ::pinConversation
        )
    )
        private set

    init {
        viewModelScope.launch {
            launch {
                conversationProvider.conversationListFlow.collect { conversationList ->
                    conversationPageViewState =
                        conversationPageViewState.copy(conversationList = conversationList.toPersistentList())
                }
            }
            launch {
                accountProvider.serverConnectStateFlow.collect { serverConnectState ->
                    conversationPageViewState =
                        conversationPageViewState.copy(serverConnectState = serverConnectState)
                }
            }
        }
        conversationProvider.refreshConversationList()
    }

    private fun onClickConversation(conversation: Conversation) {
        when (conversation.type) {
            ConversationType.C2C -> {
                ChatActivity.navTo(
                    context = context,
                    chat = Chat.C2C(id = conversation.id)
                )
            }

            ConversationType.Group -> {
                ChatActivity.navTo(
                    context = context,
                    chat = Chat.Group(id = conversation.id)
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
                    showToast(msg = result.desc)
                }
            }
        }
    }

    private fun pinConversation(conversation: Conversation, pin: Boolean) {
        viewModelScope.launch {
            when (val result = conversationProvider.pinConversation(
                conversation = conversation,
                pin = pin
            )) {
                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }

                is ActionResult.Success -> Unit
            }
        }
    }

}