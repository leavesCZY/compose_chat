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
import github.leavesczy.compose_chat.di.ComposeChat
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ConversationViewModel : BaseViewModel() {

    private val conversationProvider = ComposeChat.conversationProvider

    private val accountProvider = ComposeChat.accountProvider

    var pageViewState by mutableStateOf(
        value = ConversationPageViewState(
            listState = LazyListState(),
            serverConnectState = ServerConnectState.Idle,
            conversationList = persistentListOf(),
            isLoading = false,
            loadError = null,
            onClickConversation = ::onClickConversation,
            onDeleteConversation = ::deleteConversation,
            onPinConversation = ::pinConversation,
            onRetryLoad = ::retryLoad
        )
    )
        private set

    private var lastServerConnectState = ServerConnectState.Idle

    init {
        viewModelScope.launch {
            launch {
                combine(
                    conversationProvider.conversationListFlow,
                    conversationProvider.conversationLoadingFlow,
                    conversationProvider.conversationLoadErrorFlow
                ) { conversationList, isLoading, loadError ->
                    Triple(conversationList, isLoading, loadError)
                }.collect { (conversationList, isLoading, loadError) ->
                    pageViewState = pageViewState.copy(
                        conversationList = conversationList.toPersistentList(),
                        isLoading = isLoading,
                        loadError = loadError
                    )
                }
            }
            launch {
                accountProvider.serverConnectStateFlow.collect { serverConnectState ->
                    pageViewState = pageViewState.copy(serverConnectState = serverConnectState)
                    if (serverConnectState == ServerConnectState.Connected &&
                        lastServerConnectState != ServerConnectState.Connected &&
                        lastServerConnectState != ServerConnectState.Idle
                    ) {
                        conversationProvider.refreshConversationList()
                    }
                    lastServerConnectState = serverConnectState
                }
            }
        }
        conversationProvider.refreshConversationList()
    }

    private fun retryLoad() {
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
