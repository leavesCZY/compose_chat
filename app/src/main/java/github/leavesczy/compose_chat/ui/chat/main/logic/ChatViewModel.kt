package github.leavesczy.compose_chat.ui.chat.main.logic

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.ext.SdkExtensions.getExtensionVersion
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
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.proxy.FriendshipProvider
import github.leavesczy.compose_chat.proxy.GroupProvider
import github.leavesczy.compose_chat.proxy.MessageProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.utils.CompressImageUtils
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
class ChatViewModel(private val chat: Chat) : BaseViewModel() {

    private val messageMinInterval = 60 * 1000L

    private val allMessage = mutableListOf<Message>()

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
            listState = LazyListState(),
            messageList = persistentListOf(),
            onClickAvatar = ::onClickAvatar,
            onClickMessage = ::onClickMessage
        )
    )
        private set

    var bottomBarViewState by mutableStateOf(
        value = ChatPageBottomBarViewState(
            isPhotoPickerAvailable = isPhotoPickerAvailable(),
            inputSelector = InputSelector.NONE,
            onInputSelectorChanged = ::onInputSelectorChanged,
            sendTextMessage = ::sendTextMessage,
            sendImageMessage = ::sendImageMessage
        )
    )
        private set

    var loadMessageViewState by mutableStateOf(
        value = LoadMessageViewState(
            refreshing = false,
            loadFinish = false,
            loadMoreMessage = ::loadMoreMessage
        )
    )
        private set

    private val messageProvider: IMessageProvider = MessageProvider()

    init {
        messageProvider.startReceive(
            chat = chat,
            messageListener = messageListener
        )
        loadMoreMessage()
        viewModelScope.launch {
            launch {
                ComposeChat.accountProvider.refreshPersonProfile()
            }
            launch {
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
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageProvider.stopReceive(messageListener = messageListener)
        markMessageAsRead()
    }

    private fun loadMoreMessage() {
        viewModelScope.launch {
            val viewState = loadMessageViewState
            loadMessageViewState = viewState.copy(refreshing = true)
            val lastMessage = allMessage.lastOrNull { message ->
                message !is TimeMessage
            }
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
            loadMessageViewState = viewState.copy(
                refreshing = false,
                loadFinish = loadFinish
            )
        }
    }

    private fun onInputSelectorChanged(inputSelector: InputSelector) {
        bottomBarViewState = bottomBarViewState.copy(inputSelector = inputSelector)
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
        lateinit var sendingMessage: Message
        for (message in messageChannel) {
            when (val messageState = message.detail.state) {
                MessageState.Sending -> {
                    sendingMessage = message
                    attachNewMessage(newMessage = message)
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

    private fun resetMessageState(msgId: String, messageState: MessageState) {
        val messageIndex = allMessage.indexOfFirst { message ->
            message.detail.msgId == msgId
        }
        if (messageIndex >= 0) {
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
            chatPageViewState = chatPageViewState.copy(messageList = allMessage.toPersistentList())
        }
    }

    private fun attachNewMessage(newMessage: Message) {
        val firstMessage = allMessage.getOrNull(index = 0)
        if (firstMessage == null || newMessage.detail.milliseconds - firstMessage.detail.milliseconds > messageMinInterval) {
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
                if (allMessage[allMessage.size - 1].detail.milliseconds - newMessageList[0].detail.milliseconds > messageMinInterval) {
                    allMessage.add(TimeMessage(targetMessage = allMessage[allMessage.size - 1]))
                }
            }
            var filteredMsg = 1
            for (index in newMessageList.indices) {
                val currentMsg = newMessageList[index]
                val preMsg = newMessageList.getOrNull(index + 1)
                allMessage.add(element = currentMsg)
                if (preMsg == null || currentMsg.detail.milliseconds - preMsg.detail.milliseconds > messageMinInterval || filteredMsg >= 10) {
                    allMessage.add(TimeMessage(targetMessage = currentMsg))
                    filteredMsg = 1
                } else {
                    filteredMsg++
                }
            }
            chatPageViewState = chatPageViewState.copy(messageList = allMessage.toPersistentList())
        }
    }

    private fun markMessageAsRead() {
        messageProvider.cleanUnreadMessageCount(chat = chat)
    }

    private fun onClickAvatar(activity: Activity, message: Message) {
        val messageSenderId = message.detail.sender.id
        if (messageSenderId.isNotBlank()) {
            FriendProfileActivity.navTo(context = activity, friendId = messageSenderId)
        }
    }

    private fun onClickMessage(activity: Activity, message: Message) {
        when (message) {
            is ImageMessage -> {
                val allImageUrl = allMessage.mapNotNull { message ->
                    (message as? ImageMessage)?.previewImageUrl
                }.reversed()
                val initialImageUrl = message.previewImageUrl
                if (allImageUrl.isNotEmpty() && initialImageUrl.isNotBlank()) {
                    val initialPage = allImageUrl.indexOf(element = initialImageUrl)
                        .coerceAtLeast(minimumValue = 0)
                    PreviewImageActivity.navTo(
                        context = activity,
                        imageUriList = allImageUrl,
                        initialPage = initialPage
                    )
                }
            }

            is TextMessage, is SystemMessage, is TimeMessage -> {

            }
        }
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getExtensionVersion(Build.VERSION_CODES.R) >= 2
        } else {
            false
        }
    }

}