package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.base.provider.IMessageProvider
import github.leavesc.compose_chat.proxy.consts.AppConst
import github.leavesc.compose_chat.proxy.utils.RandomUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.SoftReference
import kotlin.coroutines.resume

/**
 * @Author: leavesC
 * @Date: 2021/10/27 14:38
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class MessageProvider : IMessageProvider, Converters {

    private val messageListenerMap =
        mutableMapOf<String, SoftReference<IMessageProvider.MessageListener>>()

    init {
        V2TIMManager.getMessageManager().addAdvancedMsgListener(object :
            V2TIMAdvancedMsgListener() {
            override fun onRecvNewMessage(msg: V2TIMMessage) {
                val partId = msg.groupID ?: msg.userID ?: ""
                val messageListener = messageListenerMap[partId]?.get()
                if (messageListener != null) {
                    val message = convertMessage(msg) ?: return
                    messageListener.onReceiveMessage(
                        message = message
                    )
                } else {
                    messageListenerMap.remove(partId)
                }
            }
        })
    }

    override fun markMessageAsRead(chat: Chat) {
        val id = chat.id
        when (chat) {
            is Chat.C2C -> {
                V2TIMManager.getMessageManager().markC2CMessageAsRead(id, null)
            }
            is Chat.Group -> {
                V2TIMManager.getMessageManager().markGroupMessageAsRead(id, null)
            }
        }
    }

    override suspend fun getHistoryMessage(
        chat: Chat,
        lastMessage: Message?
    ): LoadMessageResult {
        val chatId = chat.id
        val count = 40
        return suspendCancellableCoroutine { continuation ->
            val callback = object : V2TIMValueCallback<List<V2TIMMessage>> {
                override fun onSuccess(t: List<V2TIMMessage>) {
                    continuation.resume(
                        value = LoadMessageResult.Success(
                            messageList = convertMessage(t),
                            loadFinish = t.size < count
                        )
                    )
                }

                override fun onError(code: Int, desc: String?) {
                    continuation.resume(value = LoadMessageResult.Failed(reason = "code: $code desc: $desc"))
                }
            }
            when (chat) {
                is Chat.C2C -> {
                    V2TIMManager.getMessageManager().getC2CHistoryMessageList(
                        chatId,
                        count,
                        lastMessage?.tag as? V2TIMMessage,
                        callback
                    )
                }
                is Chat.Group -> {
                    V2TIMManager.getMessageManager().getGroupHistoryMessageList(
                        chatId,
                        count,
                        lastMessage?.tag as? V2TIMMessage,
                        callback
                    )
                }
            }
        }
    }

    private class SendMessageCallback(
        private val coroutineScope: CoroutineScope,
        private val preSendMessage: Message,
        private val messageChannel: Channel<Message>
    ) : V2TIMSendCallback<V2TIMMessage> {

        private fun Message.copyMessage(messageState: MessageState, failReason: Any?): Message {
            return when (this) {
                is TextMessage -> {
                    this.copy(
                        detail = this.detail.copy(
                            state = messageState
                        )
                    ).apply {
                        tag = failReason
                    }
                }
                is ImageMessage -> {
                    this.copy(
                        detail = this.detail.copy(
                            state = messageState
                        )
                    ).apply {
                        tag = failReason
                    }
                }
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }

        override fun onSuccess(t: V2TIMMessage) {
            coroutineScope.launch {
                messageChannel.send(
                    element = preSendMessage.copyMessage(
                        messageState = MessageState.Completed,
                        failReason = null
                    )
                )
                messageChannel.close()
            }
        }

        override fun onError(code: Int, desc: String?) {
            coroutineScope.launch {
                messageChannel.send(
                    element = preSendMessage.copyMessage(
                        messageState = MessageState.SendFailed,
                        failReason = "code: $code desc: $desc"
                    )
                )
                messageChannel.close()
            }
        }

        override fun onProgress(progress: Int) {

        }

    }

    override suspend fun sendText(chat: Chat, text: String, messageChannel: Channel<Message>) {
        val messageDetail = generatePreSendMessageDetail()
        val preSendMessage = TextMessage(
            detail = messageDetail,
            msg = text
        )
        messageChannel.send(element = preSendMessage)
        val callback = SendMessageCallback(
            coroutineScope = coroutineScope,
            preSendMessage = preSendMessage,
            messageChannel = messageChannel
        )
        when (chat) {
            is Chat.C2C -> {
                V2TIMManager.getInstance().sendC2CTextMessage(text, chat.id, callback)
            }
            is Chat.Group -> {
                V2TIMManager.getInstance()
                    .sendGroupTextMessage(text, chat.id, V2TIMMessage.V2TIM_PRIORITY_HIGH, callback)
            }
        }
    }

    override suspend fun sendImage(
        chat: Chat,
        imagePath: String,
        messageChannel: Channel<Message>
    ) {
        val messageDetail = generatePreSendMessageDetail()
        val preSendMessage = ImageMessage(
            detail = messageDetail,
            imagePath = imagePath,
        )
        messageChannel.send(element = preSendMessage)
        val imageMessage = V2TIMManager.getMessageManager().createImageMessage(imagePath)
        val callback =
            SendMessageCallback(
                coroutineScope = coroutineScope,
                preSendMessage = preSendMessage,
                messageChannel = messageChannel
            )
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
        V2TIMManager.getMessageManager().sendMessage(
            imageMessage,
            c2cId,
            groupId,
            V2TIMMessage.V2TIM_PRIORITY_HIGH,
            false,
            null,
            callback
        )
    }

    override fun startReceive(
        chat: Chat,
        messageListener: IMessageProvider.MessageListener
    ) {
        val id = chat.id
        messageListenerMap.remove(id)
        messageListenerMap[id] = SoftReference(messageListener)
        checkListener()
    }

    override fun stopReceive(messageListener: IMessageProvider.MessageListener) {
        removeReduceListener(listener = messageListener, listenerMap = messageListenerMap)
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

    private fun generatePreSendMessageDetail(): MessageDetail {
        return MessageDetail(
            msgId = RandomUtils.generateMessageId(),
            timestamp = RandomUtils.generateMessageTimestamp(),
            state = MessageState.Sending,
            sender = AppConst.personProfile.value,
            isSelfMessage = true
        )
    }

}