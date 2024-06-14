package github.leavesczy.compose_chat.proxy

import android.graphics.BitmapFactory
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSendCallback
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageElement
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.lang.ref.SoftReference
import kotlin.coroutines.resume
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MessageProvider : IMessageProvider {

    private val messageListenerMap =
        mutableMapOf<String, SoftReference<IMessageProvider.MessageListener>>()

    init {
        V2TIMManager.getMessageManager().addAdvancedMsgListener(
            object : V2TIMAdvancedMsgListener() {
                override fun onRecvNewMessage(msg: V2TIMMessage) {
                    val partId = msg.groupID ?: msg.userID ?: ""
                    val messageListener = messageListenerMap[partId]?.get()
                    if (messageListener != null) {
                        val message = Converters.convertMessage(msg)
                        messageListener.onReceiveMessage(
                            message = message
                        )
                    } else {
                        messageListenerMap.remove(partId)
                    }
                }
            }
        )
    }

    override suspend fun getHistoryMessage(chat: Chat, lastMessage: Message?): LoadMessageResult {
        val chatId = chat.id
        val count = 60
        return suspendCancellableCoroutine { continuation ->
            val callback = object : V2TIMValueCallback<List<V2TIMMessage>> {
                override fun onSuccess(t: List<V2TIMMessage>) {
                    continuation.resume(
                        value = LoadMessageResult.Success(
                            messageList = Converters.convertMessage(t),
                            loadFinish = t.size < count
                        )
                    )
                }

                override fun onError(code: Int, desc: String?) {
                    continuation.resume(
                        value = LoadMessageResult.Failed(reason = "code: $code desc: $desc")
                    )
                }
            }
            when (chat) {
                is Chat.PrivateChat -> {
                    V2TIMManager.getMessageManager().getC2CHistoryMessageList(
                        chatId,
                        count,
                        lastMessage?.tag as? V2TIMMessage, callback
                    )
                }

                is Chat.GroupChat -> {
                    V2TIMManager.getMessageManager().getGroupHistoryMessageList(
                        chatId,
                        count,
                        lastMessage?.tag as? V2TIMMessage, callback
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
            val localTempMessage = ImageMessage(
                messageDetail = generatePreSendMessageDetail(),
                original = ImageElement(options.outWidth, options.outHeight, imagePath),
                large = null,
                thumb = null
            )
            val createdMessage = V2TIMManager.getMessageManager().createImageMessage(imagePath)
            return@withContext sendMessage(
                chat = chat,
                timMessage = createdMessage,
                localTempMessage = localTempMessage
            )
        }
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
            is Chat.PrivateChat -> {
                c2cId = chat.id
                groupId = ""
            }

            is Chat.GroupChat -> {
                c2cId = ""
                groupId = chat.id
            }
        }
        messageChannel.send(localTempMessage)
        V2TIMManager.getMessageManager().sendMessage(timMessage,
            c2cId,
            groupId,
            V2TIMMessage.V2TIM_PRIORITY_HIGH,
            false,
            null,
            object : V2TIMSendCallback<V2TIMMessage> {
                override fun onSuccess(messsage: V2TIMMessage) {
                    ChatCoroutineScope.launch {
                        val convertMessage = Converters.convertMessage(messsage)
                        messageChannel.send(element = convertMessage)
                        messageChannel.close()
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    ChatCoroutineScope.launch {
                        messageChannel.send(element = localTempMessage.resetToFailed(failReason = "code: $code desc: $desc"))
                        messageChannel.close()
                    }
                }

                override fun onProgress(progress: Int) {

                }
            })
        return messageChannel
    }

    private fun Message.resetToFailed(failReason: String): Message {
        val failedState = MessageState.SendFailed(reason = failReason)
        return when (this) {
            is TextMessage -> {
                this.copy(messageDetail = this.detail.copy(state = failedState))
            }

            is ImageMessage -> {
                this.copy(messageDetail = this.detail.copy(state = failedState))
            }

            is TimeMessage -> {
                throw IllegalArgumentException()
            }

            is SystemMessage -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun startReceive(chat: Chat, messageListener: IMessageProvider.MessageListener) {
        val id = chat.id
        messageListenerMap.remove(id)
        messageListenerMap[id] = SoftReference(messageListener)
        checkListener()
    }

    override fun stopReceive(messageListener: IMessageProvider.MessageListener) {
        removeReduceListener(listener = messageListener, listenerMap = messageListenerMap)
    }

    override fun cleanConversationUnreadMessageCount(chat: Chat) {
        V2TIMManager.getConversationManager().cleanConversationUnreadMessageCount(
            Converters.getConversationKey(chat = chat),
            0,
            0,
            null
        )
    }

    private fun checkListener() {
        removeReduceListener(listener = null, listenerMap = messageListenerMap)
    }

    private fun removeReduceListener(
        listener: IMessageProvider.MessageListener?,
        listenerMap: MutableMap<String, SoftReference<IMessageProvider.MessageListener>>
    ) {
        val filter = listenerMap.filter { it.value.get() == listener }
        for (entry in filter) {
            listenerMap.remove(entry.key)
        }
    }

    private suspend fun generatePreSendMessageDetail(): MessageDetail {
        return MessageDetail(
            msgId = generateMessageId(),
            timestamp = generateMessageTimestamp(),
            state = MessageState.Sending,
            sender = Converters.getSelfProfile() ?: PersonProfile.Empty,
            isOwnMessage = true
        )
    }

    private fun generateMessageId(): String {
        return (System.currentTimeMillis() + Random.nextInt(
            1024, 2048
        )).toString()
    }

    private fun generateMessageTimestamp(): Long {
        return V2TIMManager.getInstance().serverTime
    }

}