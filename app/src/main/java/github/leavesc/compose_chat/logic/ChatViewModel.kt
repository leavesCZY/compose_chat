package github.leavesc.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.base.provider.IMessageProvider
import github.leavesc.compose_chat.model.ChatScreenState
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/10/27 14:39
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class ChatViewModel(private val chat: Chat) : ViewModel() {

    private var loadMessageJob: Job? = null

    private val allMessage = mutableListOf<Message>()

    private val lastMessage: Message?
        get() {
            return allMessage.lastOrNull { it !is TimeMessage }
        }

    private val messageListener = object : IMessageProvider.MessageListener {
        override fun onReceiveMessage(message: Message) {
            attachNewMessage(newMessage = message, mushScrollToBottom = false)
            markMessageAsRead()
        }
    }

    val chatScreenState = MutableStateFlow(
        ChatScreenState(
            messageList = emptyList(),
            showLoadMore = false,
            loadFinish = false,
            mushScrollToBottom = false
        )
    )

    val screenTopBarTitle = MutableStateFlow("")

    init {
        ComposeChat.messageProvider.startReceive(chat = chat, messageListener = messageListener)
        ComposeChat.accountProvider.refreshPersonProfile()
        when (chat) {
            is Chat.C2C -> {
                getFriendProfile(friendId = chat.id)
            }
            is Chat.Group -> {
                getGroupProfile(groupId = chat.id)
            }
        }
    }

    private fun getFriendProfile(friendId: String) {
        viewModelScope.launch {
            ComposeChat.friendshipProvider.getFriendProfile(friendId = friendId)?.let {
                screenTopBarTitle.emit(it.showName)
            }
        }
    }

    private fun getGroupProfile(groupId: String) {
        viewModelScope.launch {
            ComposeChat.groupProvider.getGroupInfo(groupId = groupId)?.let {
                screenTopBarTitle.emit(it.name)
            }
        }
    }

    fun loadMoreMessage() {
        if (loadMessageJob != null || chatScreenState.value.loadFinish) {
            return
        }
        refreshViewState(
            showLoadMore = true,
            loadFinish = false,
        )
        getHistoryMessageList()
    }

    private fun getHistoryMessageList() {
        loadMessageJob = viewModelScope.launch {
            val loadResult = ComposeChat.messageProvider.getHistoryMessage(
                chat = chat,
                lastMessage = lastMessage
            )
            when (loadResult) {
                is LoadMessageResult.Success -> {
                    addMessageToFooter(
                        newMessageList = loadResult.messageList,
                        showLoadMore = false,
                        loadFinish = loadResult.loadFinish
                    )
                }
                is LoadMessageResult.Failed -> {
                    refreshViewState(showLoadMore = false, loadFinish = false)
                }
            }
        }.apply {
            invokeOnCompletion {
                loadMessageJob = null
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val messageChannel = Channel<Message>()
            launch {
                ComposeChat.messageProvider.send(
                    chat = chat,
                    text = text,
                    channel = messageChannel
                )
            }
            var sendingMessage: Message? = null
            for (message in messageChannel) {
                when (message.state) {
                    MessageState.Sending -> {
                        sendingMessage = message
                        attachNewMessage(newMessage = message, mushScrollToBottom = true)
                    }
                    MessageState.Completed, MessageState.SendFailed -> {
                        val sending = sendingMessage ?: return@launch
                        val sendingMessageIndex =
                            allMessage.indexOfFirst { it.msgId == sending.msgId }
                        if (sendingMessageIndex > -1) {
                            allMessage[sendingMessageIndex] = message
                            refreshViewState()
                        }
                        if (message.state == MessageState.SendFailed) {
                            (message.tag as? String)?.let {
                                showToast(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun attachNewMessage(newMessage: Message, mushScrollToBottom: Boolean) {
        val firstMessage = allMessage.getOrNull(0)
        if (firstMessage == null || newMessage.timestamp - firstMessage.timestamp > 60) {
            allMessage.add(0, TimeMessage(targetMessage = newMessage))
        }
        allMessage.add(0, newMessage)
        refreshViewState(mushScrollToBottom = mushScrollToBottom)
    }

    private fun addMessageToFooter(
        newMessageList: List<Message>,
        showLoadMore: Boolean,
        loadFinish: Boolean
    ) {
        if (newMessageList.isNotEmpty()) {
            if (allMessage.isNotEmpty()) {
                if (allMessage[allMessage.size - 1].timestamp - newMessageList[0].timestamp > 60) {
                    allMessage.add(TimeMessage(targetMessage = allMessage[allMessage.size - 1]))
                }
            }
            var filteredMsg = 1
            for (index in newMessageList.indices) {
                val currentMsg = newMessageList[index]
                val preMsg = newMessageList.getOrNull(index + 1)
                allMessage.add(currentMsg)
                if (preMsg == null || currentMsg.timestamp - preMsg.timestamp > 60) {
                    allMessage.add(TimeMessage(targetMessage = currentMsg))
                    filteredMsg = 1
                } else if (filteredMsg >= 10) {
                    allMessage.add(TimeMessage(targetMessage = currentMsg))
                    filteredMsg = 1
                } else {
                    filteredMsg++
                }
            }
        }
        refreshViewState(showLoadMore = showLoadMore, loadFinish = loadFinish)
    }

    private fun refreshViewState(
        showLoadMore: Boolean = chatScreenState.value.showLoadMore,
        loadFinish: Boolean = chatScreenState.value.loadFinish,
        mushScrollToBottom: Boolean = false,
    ) {
        chatScreenState.value = ChatScreenState(
            messageList = allMessage.toList(),
            showLoadMore = showLoadMore,
            loadFinish = loadFinish,
            mushScrollToBottom = mushScrollToBottom
        )
    }

    private fun markMessageAsRead() {
        ComposeChat.messageProvider.markMessageAsRead(chat = chat)
    }

    override fun onCleared() {
        super.onCleared()
        ComposeChat.messageProvider.stopReceive(messageListener = messageListener)
        markMessageAsRead()
    }

}