package github.leavesczy.compose_chat.logic

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.common.provider.IMessageProvider
import github.leavesczy.compose_chat.model.ChatPageState
import github.leavesczy.compose_chat.utils.ContextHolder
import github.leavesczy.compose_chat.utils.ImageUtils
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            attachNewMessage(newMessage = message, mushScrollToBottom = true)
            markMessageAsRead()
        }
    }

    val chatPageState = MutableStateFlow(
        ChatPageState(
            messageList = emptyList(),
            showLoadMore = false,
            loadFinish = false,
            mushScrollToBottom = false
        )
    )

    val topBarTitle = MutableStateFlow("")

    init {
        ComposeChat.messageProvider.startReceive(chat = chat, messageListener = messageListener)
        ComposeChat.accountProvider.getPersonProfile()
        when (chat) {
            is PrivateChat -> {
                getFriendProfile(friendId = chat.id)
            }
            is GroupChat -> {
                getGroupProfile(groupId = chat.id)
            }
        }
    }

    private fun getFriendProfile(friendId: String) {
        viewModelScope.launch {
            ComposeChat.friendshipProvider.getFriendProfile(friendId = friendId)?.let {
                topBarTitle.emit(it.showName)
            }
        }
    }

    private fun getGroupProfile(groupId: String) {
        viewModelScope.launch {
            ComposeChat.groupProvider.getGroupInfo(groupId = groupId)?.let {
                topBarTitle.emit(it.name)
            }
        }
    }

    fun loadMoreMessage() {
        if (loadMessageJob != null || chatPageState.value.loadFinish) {
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

    fun sendTextMessage(text: String) {
        viewModelScope.launch {
            val messageChannel = ComposeChat.messageProvider.sendText(chat = chat, text = text)
            handleMessageChannel(messageChannel = messageChannel)
        }
    }

    fun sendImageMessage(imageUri: Uri) {
        viewModelScope.launch {
            val imagePath = withContext(Dispatchers.IO) {
                val imageFile =
                    ImageUtils.saveImageToCacheDir(
                        context = ContextHolder.context,
                        imageUri = imageUri
                    )
                imageFile?.absolutePath
            }
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
                    attachNewMessage(newMessage = message, mushScrollToBottom = true)
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
                is TimeMessage -> {
                    throw IllegalArgumentException()
                }
            }
            allMessage[index] = newMessage
            refreshViewState()
        }
    }

    private fun attachNewMessage(newMessage: Message, mushScrollToBottom: Boolean) {
        val firstMessage = allMessage.getOrNull(0)
        if (firstMessage == null || newMessage.messageDetail.timestamp - firstMessage.messageDetail.timestamp > 60) {
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
        refreshViewState(showLoadMore = showLoadMore, loadFinish = loadFinish)
    }

    private fun refreshViewState(
        showLoadMore: Boolean = chatPageState.value.showLoadMore,
        loadFinish: Boolean = chatPageState.value.loadFinish,
        mushScrollToBottom: Boolean = false,
    ) {
        chatPageState.value = ChatPageState(
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