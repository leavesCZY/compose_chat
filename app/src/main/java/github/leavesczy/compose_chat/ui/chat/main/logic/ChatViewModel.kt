package github.leavesczy.compose_chat.ui.chat.main.logic

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.BaseViewModel
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.LoadMessageResult
import github.leavesczy.compose_chat.base.models.Message
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.utils.CompressImageUtils
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(private val chat: Chat) : BaseViewModel() {

    private val messageMinInterval = 60 * 1000L

    private val allMessage = mutableListOf<Message>()

    private val imageUrlList = mutableListOf<String>()

    private val messageProvider: IMessageProvider = ComposeChat.messageProvider

    private val messageListener = object : IMessageProvider.MessageListener {
        override fun onReceiveMessage(message: Message) {
            viewModelScope.launch {
                attachNewMessage(newMessage = message)
                tryScrollToLatestMessage()
                markMessageAsRead()
            }
        }
    }

    private val scrollToLatestMessageFlow = MutableSharedFlow<Long>()

    var pageViewState by mutableStateOf(
        value = ChatPageViewState(
            chat = chat,
            listState = LazyListState(),
            scrollToLatestMessageFlow = scrollToLatestMessageFlow,
            topBarTitle = "",
            messageList = persistentListOf(),
            onClickAvatar = ::onClickAvatar,
            onClickMessage = ::onClickMessage
        )
    )
        private set

    var bottomBarViewState by mutableStateOf(
        value = ChatPageBottomBarViewState(
            inputSelector = InputSelector.None,
            onInputSelectorChanged = ::onInputSelectorChanged,
            onSendTextMessage = ::sendTextMessage,
            onSendImageMessage = ::sendImageMessage
        )
    )
        private set

    var loadMessageViewState by mutableStateOf(
        value = LoadMessageViewState(
            isRefreshing = false,
            isLoadFinished = false,
            onLoadMoreMessage = ::loadMoreMessage
        )
    )
        private set

    init {
        messageProvider.startReceive(
            chat = chat,
            messageListener = messageListener
        )
        initPartyName()
        loadMoreMessage()
    }

    override fun onCleared() {
        messageProvider.stopReceive(messageListener = messageListener)
        markMessageAsRead()
    }

    private fun initPartyName() {
        viewModelScope.launch {
            val name = when (chat) {
                is Chat.C2C -> {
                    ComposeChat.friendshipProvider.getFriendProfile(friendId = chat.id)?.showName
                }

                is Chat.Group -> {
                    ComposeChat.groupProvider.getGroupInfo(groupId = chat.id)?.name
                }
            } ?: ""
            pageViewState = pageViewState.copy(topBarTitle = name)
        }
    }

    private fun loadMoreMessage() {
        viewModelScope.launch {
            val viewState = loadMessageViewState
            loadMessageViewState = viewState.copy(isRefreshing = true)
            val lastMessage = allMessage.lastOrNull { message ->
                message !is TimeMessage
            }
            val loadResult = messageProvider.getHistoryMessage(
                chat = chat,
                lastMessage = lastMessage
            )
            val isLoadFinished = when (loadResult) {
                is LoadMessageResult.Success -> {
                    addMessageToFooter(newMessageList = loadResult.messageList)
                    loadResult.isLoadFinished
                }

                is LoadMessageResult.Failed -> {
                    false
                }
            }
            loadMessageViewState = viewState.copy(
                isRefreshing = false,
                isLoadFinished = isLoadFinished
            )
        }
    }

    private fun onInputSelectorChanged(inputSelector: InputSelector) {
        val viewState = bottomBarViewState
        if (viewState.inputSelector != inputSelector) {
            bottomBarViewState = viewState.copy(inputSelector = inputSelector)
        }
    }

    private fun sendTextMessage(text: String) {
        if (text.isBlank()) {
            return
        }
        viewModelScope.launch {
            val messageChannel = messageProvider.sendText(chat = chat, text = text)
            handleMessageChannel(messageChannel = messageChannel)
        }
    }

    private fun sendImageMessage(imageUri: Uri) {
        viewModelScope.launch {
            val imageFile = CompressImageUtils.compressImage(
                context = context,
                imageUri = imageUri
            )
            val imagePath = imageFile?.absolutePath
            if (imagePath.isNullOrBlank()) {
                showToast(resId = R.string.toast_image_fetch_failed)
            } else {
                val messageChannel = messageProvider.sendImage(chat = chat, imagePath = imagePath)
                handleMessageChannel(messageChannel = messageChannel)
            }
        }
    }

    private suspend fun handleMessageChannel(messageChannel: Channel<Message>) {
        withContext(context = Dispatchers.Main.immediate) {
            lateinit var sendingMessage: Message
            for (message in messageChannel) {
                when (val messageState = message.detail.state) {
                    MessageState.Sending -> {
                        sendingMessage = message
                        attachNewMessage(newMessage = message)
                        forceScrollToLatestMessage()
                        markMessageAsRead()
                    }

                    MessageState.Success -> {
                        resetMessageState(
                            msgId = sendingMessage.detail.msgId,
                            messageState = messageState
                        )
                    }

                    is MessageState.Failed -> {
                        resetMessageState(
                            msgId = sendingMessage.detail.msgId,
                            messageState = messageState
                        )
                        val failReason = messageState.reason
                        if (failReason.isNotBlank()) {
                            showToast(msg = failReason)
                        }
                    }
                }
            }
        }
    }

    private suspend fun resetMessageState(msgId: String, messageState: MessageState) {
        withContext(context = Dispatchers.Main.immediate) {
            val messageIndex = allMessage.indexOfFirst { message ->
                message.detail.msgId == msgId
            }
            if (messageIndex < 0) {
                return@withContext
            }
            val targetMessage = allMessage[messageIndex]
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
            allMessage[messageIndex] = newMessage
            publishMessageList()
        }
    }

    private suspend fun attachNewMessage(newMessage: Message) {
        withContext(context = Dispatchers.Main.immediate) {
            val firstMessage = allMessage.getOrNull(index = 0)
            if (firstMessage == null ||
                newMessage.detail.milliseconds - firstMessage.detail.milliseconds > messageMinInterval ||
                allMessage.take(n = 10).all {
                    when (it) {
                        is ImageMessage,
                        is SystemMessage,
                        is TextMessage -> {
                            true
                        }

                        is TimeMessage -> {
                            false
                        }
                    }
                }
            ) {
                allMessage.add(index = 0, element = TimeMessage(targetMessage = newMessage))
            }
            allMessage.add(index = 0, element = newMessage)
            if (newMessage is ImageMessage) {
                imageUrlList.add(element = newMessage.previewImageUrl)
            }
            publishMessageList()
        }
    }

    private suspend fun addMessageToFooter(newMessageList: List<Message>) {
        if (newMessageList.isEmpty()) {
            return
        }
        withContext(context = Dispatchers.Main.immediate) {
            if (allMessage.isNotEmpty()) {
                if (allMessage[allMessage.size - 1].detail.milliseconds - newMessageList[0].detail.milliseconds > messageMinInterval) {
                    allMessage.add(element = TimeMessage(targetMessage = allMessage[allMessage.size - 1]))
                }
            }
            var filteredMsg = 1
            for (index in newMessageList.indices) {
                val currentMsg = newMessageList[index]
                val preMsg = newMessageList.getOrNull(index + 1)
                allMessage.add(element = currentMsg)
                if (preMsg == null ||
                    currentMsg.detail.milliseconds - preMsg.detail.milliseconds > messageMinInterval ||
                    filteredMsg >= 10
                ) {
                    allMessage.add(element = TimeMessage(targetMessage = currentMsg))
                    filteredMsg = 1
                } else {
                    filteredMsg++
                }
            }
            rebuildImageUrlList()
            publishMessageList()
        }
    }

    private fun publishMessageList() {
        pageViewState = pageViewState.copy(messageList = allMessage.toPersistentList())
    }

    private fun rebuildImageUrlList() {
        imageUrlList.clear()
        for (index in allMessage.indices.reversed()) {
            val message = allMessage[index]
            if (message is ImageMessage) {
                imageUrlList.add(element = message.previewImageUrl)
            }
        }
    }

    private suspend fun tryScrollToLatestMessage() {
        withContext(context = Dispatchers.Main.immediate) {
            if (pageViewState.listState.firstVisibleItemIndex <= 1) {
                forceScrollToLatestMessage()
            }
        }
    }

    private suspend fun forceScrollToLatestMessage() {
        withContext(context = Dispatchers.Main.immediate) {
            scrollToLatestMessageFlow.emit(value = System.currentTimeMillis())
        }
    }

    private fun markMessageAsRead() {
        messageProvider.cleanUnreadMessageCount(chat = chat)
    }

    private fun onClickAvatar(message: Message) {
        val messageSenderId = message.detail.sender.id
        if (messageSenderId.isNotBlank()) {
            FriendProfileActivity.navTo(context = context, friendId = messageSenderId)
        }
    }

    private fun onClickMessage(message: Message) {
        when (message) {
            is ImageMessage -> {
                val initialImageUrl = message.previewImageUrl
                if (imageUrlList.isNotEmpty() && initialImageUrl.isNotBlank()) {
                    val initialPage = imageUrlList.indexOf(element = initialImageUrl)
                        .coerceAtLeast(minimumValue = 0)
                    PreviewImageActivity.navTo(
                        context = context,
                        imageUriList = imageUrlList,
                        initialPage = initialPage
                    )
                }
            }

            is TextMessage, is SystemMessage, is TimeMessage -> {

            }
        }
    }

}