package github.leavesczy.compose_chat.ui.chat.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.*
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.utils.CompressImageUtils
import github.leavesczy.compose_chat.utils.ContextHolder
import github.leavesczy.compose_chat.utils.showToast
import github.leavesczy.matisse.MediaResource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ChatViewModel(private val chat: Chat) : ViewModel() {

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

    var chatPageViewState by mutableStateOf(
        value = ChatPageViewState(
            chat = chat,
            topBarTitle = "",
            listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
            messageList = emptyList()
        )
    )
        private set

    var loadMessageViewState by mutableStateOf(
        value = LoadMessageViewState(
            refreshing = false,
            loadFinish = false
        )
    )
        private set

    init {
        ComposeChat.messageProvider.startReceive(chat = chat, messageListener = messageListener)
        ComposeChat.accountProvider.refreshPersonProfile()
        viewModelScope.launch {
            val name = when (chat) {
                is Chat.PrivateChat -> {
                    ComposeChat.friendshipProvider.getFriendProfile(friendId = chat.id)?.showName
                }
                is Chat.GroupChat -> {
                    ComposeChat.groupProvider.getGroupInfo(groupId = chat.id)?.name
                }
            } ?: ""
            chatPageViewState = chatPageViewState.copy(topBarTitle = name)
        }
        loadMoreMessage()
    }

    fun loadMoreMessage() {
        viewModelScope.launch {
            loadMessageViewState = loadMessageViewState.copy(refreshing = true)
            val loadResult: LoadMessageResult
            val loadDuration = measureTimeMillis {
                loadResult = ComposeChat.messageProvider.getHistoryMessage(
                    chat = chat,
                    lastMessage = lastMessage
                )
            }
            val delay = 500L - loadDuration
            if (delay > 0) {
                delay(timeMillis = delay)
            }
            val loadFinish = when (loadResult) {
                is LoadMessageResult.Success -> {
                    addMessageToFooter(newMessageList = loadResult.messageList)
                    loadResult.loadFinish
                }
                is LoadMessageResult.Failed -> {
                    false
                }
            }
            loadMessageViewState = LoadMessageViewState(
                refreshing = false,
                loadFinish = loadFinish
            )
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
                showToast(msg = "图片获取失败")
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
                        showToast(msg = failReason)
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
            chatPageViewState = chatPageViewState.copy(messageList = allMessage.toList())
        }
    }

    private fun attachNewMessage(newMessage: Message) {
        val firstMessage = allMessage.getOrNull(0)
        if (firstMessage == null || newMessage.messageDetail.timestamp - firstMessage.messageDetail.timestamp > 60) {
            allMessage.add(0, TimeMessage(targetMessage = newMessage))
        }
        allMessage.add(0, newMessage)
        chatPageViewState = chatPageViewState.copy(messageList = allMessage.toList())
        viewModelScope.launch {
            delay(timeMillis = 20)
            chatPageViewState.listState.scrollToItem(index = 0)
        }
    }

    private fun addMessageToFooter(newMessageList: List<Message>) {
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
                if (preMsg == null || currentMsg.messageDetail.timestamp - preMsg.messageDetail.timestamp > 60 || filteredMsg >= 10) {
                    allMessage.add(TimeMessage(targetMessage = currentMsg))
                    filteredMsg = 1
                } else {
                    filteredMsg++
                }
            }
            chatPageViewState = chatPageViewState.copy(messageList = allMessage.toList())
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