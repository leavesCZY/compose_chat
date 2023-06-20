package github.leavesczy.compose_chat.ui.chat.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.ImageMessage
import github.leavesczy.compose_chat.base.model.LoadMessageResult
import github.leavesczy.compose_chat.base.model.Message
import github.leavesczy.compose_chat.base.model.MessageState
import github.leavesczy.compose_chat.base.model.SystemMessage
import github.leavesczy.compose_chat.base.model.TextMessage
import github.leavesczy.compose_chat.base.model.TimeMessage
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.provider.ContextProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.InputSelector
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import github.leavesczy.compose_chat.utils.CompressImageUtils
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
class ChatViewModel(private val chat: Chat) : BaseViewModel() {

    companion object {

        private const val TEXT_MSG_MAX_LENGTH = 200

    }

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
        value = LoadMessageViewState(refreshing = false, loadFinish = false)
    )
        private set

    var textMessageInputted by mutableStateOf(value = TextFieldValue(text = ""))
        private set

    var currentInputSelector by mutableStateOf(value = InputSelector.NONE)
        private set

    init {
        ComposeChat.messageProvider.startReceive(
            chat = chat,
            messageListener = messageListener
        )
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
            loadMessageViewState = LoadMessageViewState(refreshing = false, loadFinish = loadFinish)
        }
    }

    fun onUserInputChanged(input: TextFieldValue) {
        val newMessage = if (textMessageInputted.text.length >= TEXT_MSG_MAX_LENGTH) {
            if (input.text.length > TEXT_MSG_MAX_LENGTH) {
                return
            }
            input
        } else {
            if (input.text.length > TEXT_MSG_MAX_LENGTH) {
                textMessageInputted.copy(
                    text = input.text.substring(
                        0, TEXT_MSG_MAX_LENGTH
                    )
                )
            } else {
                input
            }
        }
        textMessageInputted = newMessage
    }

    fun appendEmoji(emoji: String) {
        if (textMessageInputted.text.length + emoji.length > TEXT_MSG_MAX_LENGTH) {
            return
        }
        val currentText = textMessageInputted.text
        val currentSelection = textMessageInputted.selection.end
        val currentSelectedText = currentText.substring(
            0, currentSelection
        )
        val messageAppend = currentSelectedText + emoji
        val selectedAppend = messageAppend.length
        textMessageInputted = TextFieldValue(
            text = messageAppend + currentText.substring(
                currentSelection, currentText.length
            ),
            selection = TextRange(index = selectedAppend)
        )
    }

    fun onInputSelectorChanged(newSelector: InputSelector) {
        currentInputSelector = newSelector
    }

    fun sendTextMessage() {
        viewModelScope.launch {
            val text = textMessageInputted.text
            if (text.isEmpty()) {
                return@launch
            }
            textMessageInputted = TextFieldValue(text = "")
            val messageChannel = ComposeChat.messageProvider.sendText(chat = chat, text = text)
            handleMessageChannel(messageChannel = messageChannel)
        }
    }

    fun sendImageMessage(mediaResource: MediaResource) {
        viewModelScope.launch {
            val imageFile = CompressImageUtils.compressImage(
                context = ContextProvider.context,
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
                        msgId = sendingMessage.messageDetail.msgId, messageState = state
                    )
                }

                is MessageState.SendFailed -> {
                    resetMessageState(
                        msgId = sendingMessage.messageDetail.msgId, messageState = state
                    )
                    val failReason = state.reason
                    if (failReason.isNotBlank()) {
                        showToast(msg = failReason)
                    }
                }
            }
        }
    }

    private fun resetMessageState(
        msgId: String, messageState: MessageState
    ) {
        val index = allMessage.indexOfFirst { it.messageDetail.msgId == msgId }
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
            delay(timeMillis = 80)
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
        ComposeChat.conversationProvider.cleanConversationUnreadMessageCount(chat = chat)
    }

    override fun onCleared() {
        super.onCleared()
        ComposeChat.messageProvider.stopReceive(messageListener = messageListener)
        markMessageAsRead()
    }

}