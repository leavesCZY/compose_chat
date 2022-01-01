package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.base.provider.IMessageProvider
import github.leavesc.compose_chat.proxy.consts.AppConst
import github.leavesc.compose_chat.proxy.logic.Converters.Companion.convertGroupMember
import github.leavesc.compose_chat.proxy.logic.Converters.Companion.convertMessage
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

    private val c2cListenerMap =
        mutableMapOf<String, SoftReference<IMessageProvider.MessageListener>>()

    private val groupListenerMap =
        mutableMapOf<String, SoftReference<IMessageProvider.MessageListener>>()

    init {
        V2TIMManager.getInstance().addSimpleMsgListener(object : V2TIMSimpleMsgListener() {
            override fun onRecvC2CTextMessage(
                msgID: String,
                sender: V2TIMUserInfo,
                text: String
            ) {
                val friendId = sender.userID
                val listener = c2cListenerMap[friendId]?.get()
                if (listener != null) {
                    val messageDetail = MessageDetail(
                        msgId = msgID,
                        timestamp = V2TIMManager.getInstance().serverTime,
                        state = MessageState.Completed,
                        sender = PersonProfile(
                            userId = friendId,
                            faceUrl = sender.faceUrl ?: "",
                            nickname = sender.nickName ?: "",
                            remark = "",
                            signature = ""
                        ),
                        isSelfMessage = false
                    )
                    listener.onReceiveMessage(
                        message = TextMessage(
                            detail = messageDetail,
                            msg = text
                        )
                    )
                } else {
                    c2cListenerMap.remove(friendId)
                }
            }

            override fun onRecvGroupTextMessage(
                msgID: String,
                groupID: String,
                sender: V2TIMGroupMemberInfo,
                text: String
            ) {
                val listener = groupListenerMap[groupID]?.get()
                if (listener != null) {
                    val messageDetail = MessageDetail(
                        msgId = msgID,
                        timestamp = V2TIMManager.getInstance().serverTime,
                        state = MessageState.Completed,
                        sender = convertGroupMember(sender).detail,
                        isSelfMessage = false
                    )
                    listener.onReceiveMessage(
                        message = TextMessage(
                            detail = messageDetail,
                            msg = text
                        )
                    )
                } else {
                    groupListenerMap.remove(groupID)
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
                        lastMessage?.messageDetail?.tag as? V2TIMMessage,
                        callback
                    )
                }
                is Chat.Group -> {
                    V2TIMManager.getMessageManager().getGroupHistoryMessageList(
                        chatId,
                        count,
                        lastMessage?.messageDetail?.tag as? V2TIMMessage,
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

        private suspend fun onFailed(reason: String) {
            when (preSendMessage) {
                is TextMessage -> {
                    messageChannel.send(
                        element = preSendMessage.copy(detail = preSendMessage.detail.copy(
                            state =
                            MessageState.SendFailed
                        ).apply {
                            tag = reason
                        })
                    )
                }
                is ImageMessage -> {
                    messageChannel.send(
                        element = preSendMessage.copy(detail = preSendMessage.detail.copy(
                            state = MessageState.SendFailed
                        ).apply {
                            tag = reason
                        })
                    )
                }
                else -> {
                    throw IllegalArgumentException()
                }
            }
        }

        override fun onSuccess(t: V2TIMMessage) {
            coroutineScope.launch {
                val msg = convertMessage(t)
                if (msg == null) {
                    onFailed(reason = "未知错误")
                } else {
                    messageChannel.send(element = msg)
                }
                messageChannel.close()
            }
        }

        override fun onError(code: Int, desc: String?) {
            coroutineScope.launch {
                onFailed(reason = "code: $code desc: $desc")
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
        when (chat) {
            is Chat.C2C -> {
                c2cListenerMap.remove(id)
                c2cListenerMap[id] = SoftReference(messageListener)
            }
            is Chat.Group -> {
                groupListenerMap.remove(id)
                groupListenerMap[id] = SoftReference(messageListener)
            }
        }
        checkListener()
    }

    override fun stopReceive(messageListener: IMessageProvider.MessageListener) {
        removeReduceListener(listener = messageListener, listenerMap = c2cListenerMap)
        removeReduceListener(listener = messageListener, listenerMap = groupListenerMap)
    }

    private fun checkListener() {
        removeReduceListener(listener = null, listenerMap = c2cListenerMap)
        removeReduceListener(listener = null, listenerMap = groupListenerMap)
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