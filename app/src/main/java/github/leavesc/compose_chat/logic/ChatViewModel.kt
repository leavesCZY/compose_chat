package github.leavesc.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.base.provider.IC2CMessageProvider
import github.leavesc.compose_chat.model.ChatScreenState
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/3 22:43
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class ChatViewModel(private val friendId: String) : ViewModel() {

    private var loadMessageJob: Job? = null

    private val allMessage = mutableListOf<Message>()

    private val lastMessage: Message?
        get() {
            return allMessage.lastOrNull { it !is TimeMessage }
        }

    private val messageListener = object : IC2CMessageProvider.MessageListener {
        override fun onReceiveMessage(message: Message) {
            attachNewMessage(newMessage = message)
            markC2CMessageAsRead()
        }
    }

    val chatScreenState = MutableStateFlow(
        ChatScreenState(
            friendProfile = PersonProfile.Empty,
            messageList = emptyList(),
            showLoadMore = false,
            loadFinish = false,
            mushScrollToBottom = false
        )
    )

    init {
        Chat.c2cMessageProvider.startReceive(friendId = friendId, messageListener = messageListener)
        Chat.accountProvider.refreshPersonProfile()
        getFriendProfile()
        markC2CMessageAsRead()
    }

    private fun getFriendProfile() {
        viewModelScope.launch(Dispatchers.Main) {
            Chat.friendshipProvider.getFriendProfile(friendId)?.let {
                refreshViewState(friendProfile = it)
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
        loadMessageJob = viewModelScope.launch(Dispatchers.Main) {
            val loadResult = Chat.c2cMessageProvider.getHistoryMessageList(
                friendId = friendId,
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
        viewModelScope.launch(Dispatchers.Main) {
            val messageChannel = Channel<Message>()
            launch {
                Chat.c2cMessageProvider.send(
                    channel = messageChannel,
                    friendId = friendId,
                    text = text
                )
            }
            var sendingMessage: Message? = null
            for (message in messageChannel) {
                when (message.state) {
                    MessageState.Sending -> {
                        sendingMessage = message
                        attachNewMessage(newMessage = message)
                    }
                    MessageState.Completed, MessageState.SendFailed -> {
                        val sending = sendingMessage ?: return@launch
                        val sendingMessageIndex =
                            allMessage.indexOfFirst { it.msgId == sending.msgId }
                        if (sendingMessageIndex > -1) {
                            allMessage[sendingMessageIndex] = message
                            refreshViewState(mushScrollToBottom = true)
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

    private fun attachNewMessage(newMessage: Message) {
        val firstMessage = allMessage.getOrNull(0)
        if (firstMessage == null || newMessage.timestamp - firstMessage.timestamp > 60) {
            allMessage.add(0, TimeMessage(targetMessage = newMessage))
        }
        allMessage.add(0, newMessage)
        refreshViewState()
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
        friendProfile: PersonProfile = chatScreenState.value.friendProfile,
        showLoadMore: Boolean = chatScreenState.value.showLoadMore,
        loadFinish: Boolean = chatScreenState.value.loadFinish,
        mushScrollToBottom: Boolean = false,
    ) {
        chatScreenState.value = ChatScreenState(
            friendProfile = friendProfile,
            messageList = allMessage.toList(),
            showLoadMore = showLoadMore,
            loadFinish = loadFinish,
            mushScrollToBottom = mushScrollToBottom
        )
    }

    fun alreadyScrollToBottom() {
        refreshViewState()
    }

    private fun markC2CMessageAsRead() {
        Chat.c2cMessageProvider.markC2CMessageAsRead(friendId = friendId)
    }

    override fun onCleared() {
        super.onCleared()
        Chat.c2cMessageProvider.stopReceive(messageListener = messageListener)
        markC2CMessageAsRead()
    }

}