package github.leavesczy.compose_chat.logic

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.*
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.model.ChatScreenState
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

    val messageInputted = MutableStateFlow(TextFieldValue())

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

    private fun sendMessage(action: suspend (Channel<Message>) -> Unit) {
        viewModelScope.launch {
            val messageChannel = Channel<Message>()
            launch {
                action(messageChannel)
            }
            var sendingMessage: Message? = null
            for (message in messageChannel) {
                when (message.messageDetail.state) {
                    MessageState.Sending -> {
                        sendingMessage = message
                        attachNewMessage(newMessage = message, mushScrollToBottom = true)
                    }
                    MessageState.Completed, MessageState.SendFailed -> {
                        val sending = sendingMessage ?: return@launch
                        val sendingMessageIndex =
                            allMessage.indexOfFirst { it.messageDetail.msgId == sending.messageDetail.msgId }
                        if (sendingMessageIndex > -1) {
                            allMessage[sendingMessageIndex] = message
                            refreshViewState()
                        }
                        if (message.messageDetail.state == MessageState.SendFailed) {
                            val failReason = message.tag as? String
                            if (!failReason.isNullOrBlank()) {
                                showToast(failReason)
                            }
                        }
                    }
                }
            }
        }
    }

    fun sendTextMessage(text: String) {
        sendMessage {
            ComposeChat.messageProvider.sendText(
                chat = chat,
                text = text,
                messageChannel = it
            )
        }
    }

    fun sendImageMessage(imageUri: Uri) {
        sendMessage {
            withContext(Dispatchers.IO) {
                val imageFile =
                    ImageUtils.saveImageToCacheDir(
                        context = ContextHolder.context,
                        imageUri = imageUri
                    )
                val imagePath = imageFile?.absolutePath
                if (imagePath.isNullOrBlank()) {
                    showToast("图片获取失败")
                } else {
                    ComposeChat.messageProvider.sendImage(
                        chat = chat,
                        imagePath = imagePath,
                        messageChannel = it
                    )
                }
            }
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

    fun onInputChange(message: TextFieldValue) {
        viewModelScope.launch {
            messageInputted.emit(message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ComposeChat.messageProvider.stopReceive(messageListener = messageListener)
        markMessageAsRead()
    }

}