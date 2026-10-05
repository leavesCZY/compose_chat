package github.leavesczy.compose_chat.ui.chat.logic

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.LoadMessageResult
import github.leavesczy.compose_chat.base.models.Message
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.di.ComposeChat
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
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
            onClickMessage = ::onClickMessage,
            onResendMessage = ::resendMessage
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
            loadError = null,
            onLoadMoreMessage = ::loadMoreMessage,
            onRetryLoad = ::loadMoreMessage
        )
    )
        private set

    init {
        messageProvider.startReceive(
            chat = chat,
            messageListener = messageListener
        )
        initPeerName()
        loadMoreMessage()
    }

    override fun onCleared() {
        messageProvider.stopReceive(messageListener = messageListener)
        markMessageAsRead()
        CompressImageUtils.cleanupOldTempFiles(context = context)
    }

    private fun initPeerName() {
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
        val viewState = loadMessageViewState
        if (viewState.isRefreshing || viewState.isLoadFinished) {
            return
        }
        loadMessageViewState = viewState.copy(isRefreshing = true, loadError = null)
        viewModelScope.launch {
            val lastMessage = allMessage.lastOrNull { message ->
                message !is TimeMessage
            }
            val loadResult = messageProvider.getHistoryMessage(
                chat = chat,
                lastMessage = lastMessage
            )
            when (loadResult) {
                is LoadMessageResult.Success -> {
                    addMessageToFooter(newMessageList = loadResult.messageList)
                    loadMessageViewState = loadMessageViewState.copy(
                        isRefreshing = false,
                        isLoadFinished = loadResult.isLoadFinished,
                        loadError = null
                    )
                }

                is LoadMessageResult.Failed -> {
                    val cursorLost = loadResult.reason == getString(
                        resId = github.leavesczy.compose_chat.base.R.string.error_load_message_cursor_lost
                    )
                    if (cursorLost && lastMessage != null) {
                        resetMessageListForReload()
                        loadMessageViewState = loadMessageViewState.copy(
                            isRefreshing = false,
                            isLoadFinished = false,
                            loadError = null
                        )
                        loadMoreMessage()
                        return@launch
                    }
                    loadMessageViewState = loadMessageViewState.copy(
                        isRefreshing = false,
                        isLoadFinished = false,
                        loadError = loadResult.reason
                    )
                    showToast(msg = loadResult.reason)
                }
            }
        }
    }

    private suspend fun resetMessageListForReload() {
        withContext(context = Dispatchers.Main.immediate) {
            allMessage.clear()
            imageUrlList.clear()
            pageViewState = pageViewState.copy(messageList = persistentListOf())
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
                var sendSucceeded = false
                try {
                    val messageChannel =
                        messageProvider.sendImage(chat = chat, imagePath = imagePath)
                    sendSucceeded = handleMessageChannel(messageChannel = messageChannel)
                } finally {
                    if (!sendSucceeded) {
                        CompressImageUtils.protectTempFile(file = imageFile)
                    }
                }
            }
        }
    }

    private fun resendMessage(message: Message) {
        if (message.detail.state !is MessageState.Failed) {
            return
        }
        viewModelScope.launch {
            resetMessageState(
                msgId = message.detail.msgId,
                messageState = MessageState.Sending
            )
            val messageChannel = messageProvider.resendMessage(chat = chat, message = message)
            handleResendMessageChannel(
                msgId = message.detail.msgId,
                messageChannel = messageChannel
            )
        }
    }

    private suspend fun handleMessageChannel(messageChannel: Channel<Message>): Boolean {
        return withContext(context = Dispatchers.Main.immediate) {
            var sendingMessage: Message? = null
            var succeeded = false
            for (message in messageChannel) {
                when (val messageState = message.detail.state) {
                    MessageState.Sending -> {
                        sendingMessage = message
                        attachNewMessage(newMessage = message)
                        forceScrollToLatestMessage()
                        markMessageAsRead()
                    }

                    MessageState.Success -> {
                        succeeded = true
                        val oldMsgId = sendingMessage?.detail?.msgId
                        if (oldMsgId.isNullOrBlank()) {
                            attachNewMessage(newMessage = message)
                        } else {
                            replaceMessage(
                                oldMsgId = oldMsgId,
                                newMessage = message
                            )
                        }
                    }

                    is MessageState.Failed -> {
                        val oldMsgId = sendingMessage?.detail?.msgId
                        if (!oldMsgId.isNullOrBlank()) {
                            resetMessageState(
                                msgId = oldMsgId,
                                messageState = messageState
                            )
                        }
                        val failReason = messageState.reason
                        if (failReason.isNotBlank()) {
                            showToast(msg = failReason)
                        }
                    }
                }
            }
            succeeded
        }
    }

    private suspend fun handleResendMessageChannel(
        msgId: String,
        messageChannel: Channel<Message>
    ) {
        withContext(context = Dispatchers.Main.immediate) {
            for (message in messageChannel) {
                when (val messageState = message.detail.state) {
                    MessageState.Sending -> {
                        resetMessageState(
                            msgId = msgId,
                            messageState = messageState
                        )
                    }

                    MessageState.Success -> {
                        replaceMessage(
                            oldMsgId = msgId,
                            newMessage = message
                        )
                    }

                    is MessageState.Failed -> {
                        resetMessageState(
                            msgId = msgId,
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
            publishMessageUpdate(index = messageIndex, message = newMessage)
        }
    }

    private suspend fun replaceMessage(oldMsgId: String, newMessage: Message) {
        withContext(context = Dispatchers.Main.immediate) {
            val messageIndex = allMessage.indexOfFirst { message ->
                message.detail.msgId == oldMsgId
            }
            if (messageIndex < 0) {
                if (allMessage.none { message -> message.detail.msgId == newMessage.detail.msgId }) {
                    attachNewMessage(newMessage = newMessage)
                }
                return@withContext
            }
            val oldMessage = allMessage[messageIndex]
            val resolvedMessage = preserveLocalImagePreview(
                oldMessage = oldMessage,
                newMessage = newMessage
            )
            if (oldMessage is ImageMessage && resolvedMessage !is ImageMessage) {
                imageUrlList.remove(element = oldMessage.previewImageUrl)
            }
            allMessage[messageIndex] = resolvedMessage
            if (resolvedMessage is ImageMessage) {
                val oldUrl = (oldMessage as? ImageMessage)?.previewImageUrl
                if (!oldUrl.isNullOrBlank()) {
                    val urlIndex = imageUrlList.indexOf(element = oldUrl)
                    if (urlIndex >= 0) {
                        imageUrlList[urlIndex] = resolvedMessage.previewImageUrl
                    } else {
                        imageUrlList.add(element = resolvedMessage.previewImageUrl)
                    }
                } else {
                    imageUrlList.add(element = resolvedMessage.previewImageUrl)
                }
            }
            publishMessageUpdate(index = messageIndex, message = resolvedMessage)
        }
    }

    private fun preserveLocalImagePreview(oldMessage: Message, newMessage: Message): Message {
        if (oldMessage is ImageMessage && newMessage is ImageMessage && oldMessage.isLocalPreview) {
            return oldMessage.withPreservedPreview(messageDetail = newMessage.detail)
        }
        return newMessage
    }

    private suspend fun attachNewMessage(newMessage: Message) {
        withContext(context = Dispatchers.Main.immediate) {
            val existIndex = allMessage.indexOfFirst { message ->
                message.detail.msgId == newMessage.detail.msgId
            }
            if (existIndex >= 0) {
                allMessage[existIndex] = newMessage
                publishMessageUpdate(index = existIndex, message = newMessage)
                return@withContext
            }
            val prepended = mutableListOf<Message>()
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
                val timeMessage = TimeMessage(targetMessage = newMessage)
                allMessage.add(index = 0, element = timeMessage)
                prepended.add(element = timeMessage)
            }
            allMessage.add(index = 0, element = newMessage)
            prepended.add(index = 0, element = newMessage)
            if (newMessage is ImageMessage) {
                imageUrlList.add(element = newMessage.previewImageUrl)
            }
            publishMessagePrepend(messages = prepended)
        }
    }

    private suspend fun addMessageToFooter(newMessageList: List<Message>) {
        if (newMessageList.isEmpty()) {
            return
        }
        withContext(context = Dispatchers.Main.immediate) {
            val existingIds = allMessage.asSequence()
                .map { message -> message.detail.msgId }
                .filter { msgId -> msgId.isNotBlank() }
                .toHashSet()
            val filteredList = newMessageList.filter { message ->
                val msgId = message.detail.msgId
                msgId.isBlank() || existingIds.add(msgId)
            }
            if (filteredList.isEmpty()) {
                return@withContext
            }
            val startIndex = allMessage.size
            if (allMessage.isNotEmpty()) {
                if (allMessage[allMessage.size - 1].detail.milliseconds - filteredList[0].detail.milliseconds > messageMinInterval) {
                    allMessage.add(element = TimeMessage(targetMessage = allMessage[allMessage.size - 1]))
                }
            }
            var filteredMsg = 1
            for (index in filteredList.indices) {
                val currentMsg = filteredList[index]
                val preMsg = filteredList.getOrNull(index + 1)
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
            publishMessageAppend(fromIndex = startIndex)
        }
    }

    private fun publishMessageUpdate(index: Int, message: Message) {
        val currentList = pageViewState.messageList
        pageViewState = pageViewState.copy(
            messageList = if (index in currentList.indices) {
                currentList.replacingAt(index = index, element = message)
            } else {
                allMessage.toPersistentList()
            }
        )
    }

    private fun publishMessagePrepend(messages: List<Message>) {
        if (messages.isEmpty()) {
            return
        }
        pageViewState = pageViewState.copy(
            messageList = pageViewState.messageList.addingAllAt(index = 0, c = messages)
        )
    }

    private fun publishMessageAppend(fromIndex: Int) {
        if (fromIndex >= allMessage.size) {
            return
        }
        pageViewState = pageViewState.copy(
            messageList = pageViewState.messageList.addingAll(
                elements = allMessage.subList(
                    fromIndex,
                    allMessage.size
                )
            )
        )
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
                    if (initialPage < 0) {
                        return
                    }
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
