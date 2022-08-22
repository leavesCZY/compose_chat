package github.leavesczy.compose_chat.ui.chat.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.common.provider.IMessageProvider
import github.leavesczy.compose_chat.model.ChatPageViewState
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.utils.CompressImageUtils
import github.leavesczy.compose_chat.utils.ContextHolder
import github.leavesczy.compose_chat.utils.showToast
import github.leavesczy.matisse.MediaResource
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/10/27 14:39
 * @Desc:
 * @Github：https://github.com/leavesCZY
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
            attachNewMessage(newMessage = message)
            markMessageAsRead()
        }
    }

    private val _chatPageViewState = MutableStateFlow(
        ChatPageViewState(
            chat = chat,
            listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
            topBarTitle = "",
            messageList = emptyList(),
            showLoadMore = false,
            loadFinish = false,
        )
    )

    val chatPageViewState: StateFlow<ChatPageViewState> = _chatPageViewState

    private val _mushScrollToBottom = MutableStateFlow(false)

    val mushScrollToBottom: StateFlow<Boolean> = _mushScrollToBottom

    init {
        ComposeChat.messageProvider.startReceive(chat = chat, messageListener = messageListener)
        ComposeChat.accountProvider.getPersonProfile()
        viewModelScope.launch {
            val name = when (chat) {
                is PrivateChat -> {
                    ComposeChat.friendshipProvider.getFriendProfile(friendId = chat.id)?.showName
                }
                is GroupChat -> {
                    ComposeChat.groupProvider.getGroupInfo(groupId = chat.id)?.name
                }
            } ?: ""
            refreshViewState(topBarTitle = name)
        }
    }

    fun loadMoreMessage() {
        if (loadMessageJob != null || chatPageViewState.value.loadFinish) {
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

    fun sendTextMessage(text: String) {
        viewModelScope.launch {
            val messageChannel = ComposeChat.messageProvider.sendText(chat = chat, text = text)
            handleMessageChannel(messageChannel = messageChannel)
        }
    }

    fun sendImageMessage(mediaResource: MediaResource) {
        viewModelScope.launch {
            val imageFile = CompressImageUtils.compressImage(
                context = ContextHolder.context,
                mediaResource = mediaResource
            )
            val imagePath = imageFile?.absolutePath
            if (imagePath.isNullOrBlank()) {
                showToast("图片获取失败")
            } else {
                val messageChannel =
                    ComposeChat.messageProvider.sendImage(chat = chat, imagePath = imagePath)
                handleMessageChannel(messageChannel = messageChannel)
            }
        }
    }

    private suspend fun handleMessageChannel(messageChannel: Channel<Message>) {
        lateinit var sendingMessage: Message
        for (message in messageChannel) {
            when (val state = message.messageDetail.state) {
                MessageState.Sending -> {
                    sendingMessage = message
                    attachNewMessage(newMessage = message)
                }
                MessageState.Completed -> {
                    resetMessageState(
                        msgId = sendingMessage.messageDetail.msgId,
                        messageState = state
                    )
                }
                is MessageState.SendFailed -> {
                    resetMessageState(
                        msgId = sendingMessage.messageDetail.msgId,
                        messageState = state
                    )
                    val failReason = state.reason
                    if (failReason.isNotBlank()) {
                        showToast(failReason)
                    }
                }
            }
        }
    }

    private fun resetMessageState(msgId: String, messageState: MessageState) {
        val index =
            allMessage.indexOfFirst { it.messageDetail.msgId == msgId }
        if (index >= 0) {
            val targetMessage = allMessage[index]
            val messageDetail = targetMessage.messageDetail
            val newMessage = when (targetMessage) {
                is ImageMessage -> {
                    targetMessage.copy(detail = messageDetail.copy(state = messageState))
                }
                is TextMessage -> {
                    targetMessage.copy(detail = messageDetail.copy(state = messageState))
                }
                is SystemMessage, is TimeMessage -> {
                    throw IllegalArgumentException()
                }
            }
            allMessage[index] = newMessage
            refreshViewState()
        }
    }

    private fun attachNewMessage(newMessage: Message) {
        val firstMessage = allMessage.getOrNull(0)
        if (firstMessage == null || newMessage.messageDetail.timestamp - firstMessage.messageDetail.timestamp > 60) {
            allMessage.add(0, TimeMessage(targetMessage = newMessage))
        }
        allMessage.add(0, newMessage)
        refreshViewState()
        viewModelScope.launch {
            delay(timeMillis = 50)
            _mushScrollToBottom.emit(value = !_mushScrollToBottom.value)
        }
    }

    private fun addMessageToFooter(
        newMessageList: List<Message>,
        loadFinish: Boolean
    ) {
        if (newMessageList.isNotEmpty()) {
            if (allMessage.isNotEmpty()) {
                if (allMessage[allMessage.size - 1].messageDetail.timestamp - newMessageList[0].messageDetail.timestamp > 60) {
                    allMessage.add(TimeMessage(targetMessage = allMessage[allMessage.size - 1]))
                }
            }
            var filteredMsg = 1
            for (index in newMessageList.indices) {
                val currentMsg = newMessageList[index]
                val preMsg = newMessageList.getOrNull(index + 1)
                allMessage.add(currentMsg)
                if (preMsg == null || currentMsg.messageDetail.timestamp - preMsg.messageDetail.timestamp > 60) {
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
        refreshViewState(showLoadMore = false, loadFinish = loadFinish)
    }

    private fun refreshViewState(
        topBarTitle: String = chatPageViewState.value.topBarTitle,
        showLoadMore: Boolean = chatPageViewState.value.showLoadMore,
        loadFinish: Boolean = chatPageViewState.value.loadFinish
    ) {
        viewModelScope.launch {
            _chatPageViewState.emit(
                value = _chatPageViewState.value.copy(
                    topBarTitle = topBarTitle,
                    messageList = allMessage.toList(),
                    showLoadMore = showLoadMore,
                    loadFinish = loadFinish
                )
            )
        }
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