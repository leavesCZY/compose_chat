package github.leavesczy.compose_chat.proxy

import android.graphics.BitmapFactory
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSendCallback
import github.leavesczy.compose_chat.base.R
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.LoadMessageResult
import github.leavesczy.compose_chat.base.models.Message
import github.leavesczy.compose_chat.base.models.MessageDetail
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.base.utils.StringResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume

object MessageProvider : IMessageProvider {

    private val listenerLock = Any()

    private val messageListenerMap =
        mutableMapOf<String, IMessageProvider.MessageListener>()

    @Volatile
    private var sessionGeneration = 0

    init {
        V2TIMManager.getMessageManager().addAdvancedMsgListener(
            object : V2TIMAdvancedMsgListener() {
                override fun onRecvNewMessage(msg: V2TIMMessage) {
                    val key = listenerKey(timMessage = msg) ?: return
                    val messageListener = synchronized(listenerLock) {
                        messageListenerMap[key]
                    } ?: return
                    val message = Converters.convertMessage(timMessage = msg)
                    messageListener.onReceiveMessage(message = message)
                }
            }
        )
    }

    override suspend fun getHistoryMessage(chat: Chat, lastMessage: Message?): LoadMessageResult {
        val chatId = chat.id
        val count = 60
        val lastTimMessage = if (lastMessage == null) {
            null
        } else {
            val stored = TimMessageStore.get(msgId = lastMessage.detail.msgId)
            if (stored == null) {
                return LoadMessageResult.Failed(
                    reason = StringResources.getString(
                        resId = R.string.error_load_message_cursor_lost
                    )
                )
            }
            stored
        }
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val callback = object : com.tencent.imsdk.v2.V2TIMValueCallback<List<V2TIMMessage>> {
                override fun onSuccess(list: List<V2TIMMessage>) {
                    if (!continuation.isActive) {
                        return
                    }
                    continuation.resume(
                        value = LoadMessageResult.Success(
                            messageList = Converters.convertMessage(messageList = list),
                            isLoadFinished = list.size < count
                        )
                    )
                }

                override fun onError(code: Int, desc: String?) {
                    if (!continuation.isActive) {
                        return
                    }
                    continuation.resume(
                        value = LoadMessageResult.Failed(
                            reason = StringResources.getString(
                                resId = R.string.error_load_message,
                                code,
                                desc ?: ""
                            )
                        )
                    )
                }
            }
            when (chat) {
                is Chat.C2C -> {
                    V2TIMManager.getMessageManager().getC2CHistoryMessageList(
                        chatId,
                        count,
                        lastTimMessage,
                        callback
                    )
                }

                is Chat.Group -> {
                    V2TIMManager.getMessageManager().getGroupHistoryMessageList(
                        chatId,
                        count,
                        lastTimMessage,
                        callback
                    )
                }
            }
        }
    }

    override suspend fun sendText(chat: Chat, text: String): Channel<Message> {
        val localTempMessage =
            TextMessage(messageDetail = generatePreSendMessageDetail(), text = text)
        val createdMessage = V2TIMManager.getMessageManager().createTextMessage(text)
        return sendMessage(
            chat = chat,
            timMessage = createdMessage,
            localTempMessage = localTempMessage
        )
    }

    override suspend fun sendImage(chat: Chat, imagePath: String): Channel<Message> {
        return withContext(context = Dispatchers.Default) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)
            val width = options.outWidth.coerceAtLeast(minimumValue = 0)
            val height = options.outHeight.coerceAtLeast(minimumValue = 0)
            val localTempMessage = ImageMessage(
                messageDetail = generatePreSendMessageDetail(),
                original = ImageMessage.ImageElement(
                    width = width,
                    height = height,
                    url = imagePath
                ),
                large = null,
                thumb = null
            )
            val createdMessage = V2TIMManager.getMessageManager().createImageMessage(imagePath)
            sendMessage(
                chat = chat,
                timMessage = createdMessage,
                localTempMessage = localTempMessage
            )
        }
    }

    override suspend fun resendMessage(chat: Chat, message: Message): Channel<Message> {
        val timMessage = TimMessageStore.get(msgId = message.detail.msgId)
        if (timMessage == null) {
            val failedChannel = Channel<Message>(capacity = 1)
            failedChannel.send(
                element = message.withState(
                    state = MessageState.Failed(
                        reason = StringResources.getString(resId = R.string.message_send_failed_unknown)
                    )
                )
            )
            failedChannel.close()
            return failedChannel
        }
        val localTempMessage = message.withState(state = MessageState.Sending)
        return sendMessage(
            chat = chat,
            timMessage = timMessage,
            localTempMessage = localTempMessage
        )
    }

    private suspend fun sendMessage(
        chat: Chat,
        timMessage: V2TIMMessage,
        localTempMessage: Message
    ): Channel<Message> {
        val messageChannel = Channel<Message>(capacity = 2)
        val c2cId: String
        val groupId: String
        when (chat) {
            is Chat.C2C -> {
                c2cId = chat.id
                groupId = ""
            }

            is Chat.Group -> {
                c2cId = ""
                groupId = chat.id
            }
        }
        val localMsgId = localTempMessage.detail.msgId
        val session = sessionGeneration
        TimMessageStore.put(msgId = localMsgId, timMessage = timMessage)
        messageChannel.send(localTempMessage)
        V2TIMManager.getMessageManager().sendMessage(
            timMessage,
            c2cId,
            groupId,
            V2TIMMessage.V2TIM_PRIORITY_HIGH,
            false,
            null,
            object : V2TIMSendCallback<V2TIMMessage> {
                override fun onSuccess(message: V2TIMMessage) {
                    AppCoroutineScope.launch {
                        if (session != sessionGeneration) {
                            messageChannel.close()
                            return@launch
                        }
                        val convertMessage = Converters.convertMessage(timMessage = message)
                        val remoteMsgId = convertMessage.detail.msgId
                        TimMessageStore.put(msgId = remoteMsgId, timMessage = message)
                        TimMessageStore.put(msgId = localMsgId, timMessage = message)
                        TimMessageStore.putAlias(localMsgId = localMsgId, remoteMsgId = remoteMsgId)
                        messageChannel.send(element = convertMessage)
                        messageChannel.close()
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    AppCoroutineScope.launch {
                        if (session != sessionGeneration) {
                            messageChannel.close()
                            return@launch
                        }
                        messageChannel.send(
                            element = localTempMessage.withState(
                                state = MessageState.Failed(
                                    reason = StringResources.getString(
                                        resId = R.string.message_send_failed_detail,
                                        code,
                                        desc ?: ""
                                    )
                                )
                            )
                        )
                        messageChannel.close()
                    }
                }

                override fun onProgress(progress: Int) {

                }
            }
        )
        return messageChannel
    }

    private fun Message.withState(state: MessageState): Message {
        return when (this) {
            is TextMessage -> {
                this.copy(messageDetail = this.detail.copy(state = state))
            }

            is ImageMessage -> {
                this.copy(messageDetail = this.detail.copy(state = state))
            }

            is TimeMessage, is SystemMessage -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun startReceive(chat: Chat, messageListener: IMessageProvider.MessageListener) {
        val key = listenerKey(chat = chat)
        synchronized(listenerLock) {
            messageListenerMap[key] = messageListener
        }
    }

    override fun stopReceive(messageListener: IMessageProvider.MessageListener) {
        synchronized(listenerLock) {
            val keys = messageListenerMap.filter { entry ->
                entry.value === messageListener
            }.keys.toList()
            keys.forEach { key ->
                messageListenerMap.remove(key = key)
            }
        }
    }

    override fun clearSessionState() {
        sessionGeneration++
        synchronized(listenerLock) {
            messageListenerMap.clear()
        }
    }

    override fun cleanUnreadMessageCount(chat: Chat) {
        V2TIMManager.getConversationManager().cleanConversationUnreadMessageCount(
            Converters.getConversationKey(chat = chat),
            0,
            0,
            null
        )
    }

    private suspend fun generatePreSendMessageDetail(): MessageDetail {
        return MessageDetail(
            msgId = generateMessageId(),
            milliseconds = generateMessageTimestamp(),
            state = MessageState.Sending,
            sender = Converters.getSelfProfile() ?: PersonProfile.Empty,
            isOwnMessage = true
        )
    }

    private fun generateMessageId(): String {
        return UUID.randomUUID().toString()
    }

    private fun generateMessageTimestamp(): Long {
        return V2TIMManager.getInstance().serverTime * 1000L
    }

    private fun listenerKey(chat: Chat): String {
        return when (chat) {
            is Chat.C2C -> "c2c_${chat.id}"
            is Chat.Group -> "group_${chat.id}"
        }
    }

    private fun listenerKey(timMessage: V2TIMMessage): String? {
        val groupId = timMessage.groupID
        if (!groupId.isNullOrBlank()) {
            return "group_$groupId"
        }
        val userId = timMessage.userID
        if (!userId.isNullOrBlank()) {
            return "c2c_$userId"
        }
        return null
    }

}
