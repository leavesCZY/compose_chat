package github.leavesczy.compose_chat.ui.chat.main.logic

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.LoadMessageResult
import github.leavesczy.compose_chat.base.models.Message
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.proxy.FriendshipProvider
import github.leavesczy.compose_chat.proxy.GroupProvider
import github.leavesczy.compose_chat.proxy.MessageProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.main.InputSelector
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import github.leavesczy.compose_chat.utils.CompressImageUtils
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            messageList = persistentListOf()
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

    var textMessageInputted by mutableStateOf(value = TextFieldValue(text = ""))
        private set

    var currentInputSelector by mutableStateOf(value = InputSelector.NONE)
        private set

    private val messageProvider: IMessageProvider = MessageProvider()

    init {
        messageProvider.startReceive(
            chat = chat,
            messageListener = messageListener
        )
        ComposeChat.accountProvider.refreshPersonProfile()
        viewModelScope.launch {
            val name = when (chat) {
                is Chat.C2C -> {
                    val friendshipProvider: IFriendshipProvider = FriendshipProvider()
                    friendshipProvider.getFriendProfile(friendId = chat.id)?.showName
                }

                is Chat.Group -> {
                    val groupProvider: IGroupProvider = GroupProvider()
                    groupProvider.getGroupInfo(groupId = chat.id)?.name
                }
            } ?: ""
            chatPageViewState = chatPageViewState.copy(topBarTitle = name)
        }
        loadMoreMessage()
    }

    fun loadMoreMessage() {
        viewModelScope.launch {
            loadMessageViewState = loadMessageViewState.copy(refreshing = true)
            val loadResult = messageProvider.getHistoryMessage(
                chat = chat,
                lastMessage = lastMessage
            )
            val loadFinish = when (loadResult) {
                is LoadMessageResult.Success -> {
                    addMessageToFooter(newMessageList = loadResult.messageList)
                    loadResult.loadFinish
                }

                is LoadMessageResult.Failed -> {
                    false
                }
            }
            loadMessageViewState = loadMessageViewState.copy(
                refreshing = false,
                loadFinish = loadFinish
            )
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
                textMessageInputted.copy(text = input.text.substring(0, TEXT_MSG_MAX_LENGTH))
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
        val currentSelectedText = currentText.take(n = currentSelection)
        val messageAppend = currentSelectedText + emoji
        val selectedAppend = messageAppend.length
        textMessageInputted = TextFieldValue(
            text = messageAppend + currentText.substring(currentSelection, currentText.length),
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
            val messageChannel = messageProvider.sendText(chat = chat, text = text)
            handleMessageChannel(messageChannel = messageChannel)
        }
    }

    fun sendImageMessage(imageUri: Uri) {
        viewModelScope.launch {
            val imageFile = CompressImageUtils.compressImage(
                context = context,
                imageUri = imageUri
            )
            val imagePath = imageFile?.absolutePath
            if (imagePath.isNullOrBlank()) {
                showToast(msg = "图片获取失败")
            } else {
                val messageChannel = messageProvider.sendImage(chat = chat, imagePath = imagePath)
                handleMessageChannel(messageChannel = messageChannel)
            }
        }
    }

    private suspend fun handleMessageChannel(messageChannel: Channel<Message>) {
        lateinit var sendingMessage: Message
        for (message in messageChannel) {
            when (val state = message.detail.state) {
                MessageState.Sending -> {
                    sendingMessage = message
                    attachNewMessage(newMessage = message)
                }

                MessageState.Completed -> {
                    resetMessageState(
                        msgId = sendingMessage.detail.msgId, messageState = state
                    )
                }

                is MessageState.SendFailed -> {
                    resetMessageState(
                        msgId = sendingMessage.detail.msgId, messageState = state
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
        val index = allMessage.indexOfFirst { it.detail.msgId == msgId }
        if (index >= 0) {
            val targetMessage = allMessage[index]
            val messageDetail = targetMessage.detail
            val newMessage = when (targetMessage) {
                is ImageMessage -> {
                    targetMessage.copy(messageDetail = messageDetail.copy(state = messageState))
                }

                is TextMessage -> {
                    targetMessage.copy(messageDetail = messageDetail.copy(state = messageState))
                }

                is SystemMessage, is TimeMessage -> {
                    throw IllegalArgumentException()
                }
            }
            allMessage[index] = newMessage
            chatPageViewState = chatPageViewState.copy(messageList = allMessage.toPersistentList())
        }
    }

    private fun attachNewMessage(newMessage: Message) {
        val firstMessage = allMessage.getOrNull(index = 0)
        if (firstMessage == null || newMessage.detail.timestamp - firstMessage.detail.timestamp > 60) {
            allMessage.add(index = 0, element = TimeMessage(targetMessage = newMessage))
        }
        allMessage.add(index = 0, element = newMessage)
        chatPageViewState = chatPageViewState.copy(messageList = allMessage.toPersistentList())
        viewModelScope.launch {
            delay(timeMillis = 80L)
            chatPageViewState.listState.scrollToItem(index = 0)
        }
    }

    private fun addMessageToFooter(newMessageList: List<Message>) {
        if (newMessageList.isNotEmpty()) {
            if (allMessage.isNotEmpty()) {
                if (allMessage[allMessage.size - 1].detail.timestamp - newMessageList[0].detail.timestamp > 60) {
                    allMessage.add(TimeMessage(targetMessage = allMessage[allMessage.size - 1]))
                }
            }
            var filteredMsg = 1
            for (index in newMessageList.indices) {
                val currentMsg = newMessageList[index]
                val preMsg = newMessageList.getOrNull(index + 1)
                allMessage.add(currentMsg)
                if (preMsg == null || currentMsg.detail.timestamp - preMsg.detail.timestamp > 60 || filteredMsg >= 10) {
                    allMessage.add(TimeMessage(targetMessage = currentMsg))
                    filteredMsg = 1
                } else {
                    filteredMsg++
                }
            }
            chatPageViewState = chatPageViewState.copy(messageList = allMessage.toPersistentList())
        }
    }

    fun filterAllImageMessageUrl(): List<String> {
        return allMessage.mapNotNull {
            (it as? ImageMessage)?.previewImageUrl
        }.reversed()
    }

    private fun markMessageAsRead() {
        messageProvider.cleanConversationUnreadMessageCount(chat = chat)
    }

    override fun onCleared() {
        super.onCleared()
        messageProvider.stopReceive(messageListener = messageListener)
        markMessageAsRead()
    }

}