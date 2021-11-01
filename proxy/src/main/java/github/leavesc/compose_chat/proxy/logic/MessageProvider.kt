package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.base.provider.IMessageProvider
import github.leavesc.compose_chat.proxy.consts.AppConst
import github.leavesc.compose_chat.proxy.utils.RandomUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
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
                    listener.onReceiveMessage(
                        TextMessage.FriendTextMessage(
                            msgId = msgID,
                            msg = text,
                            timestamp = V2TIMManager.getInstance().serverTime,
                            sender = PersonProfile(
                                userId = friendId,
                                faceUrl = sender.faceUrl ?: "",
                                nickname = sender.nickName ?: "",
                                remark = "",
                                signature = ""
                            )
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
                    listener.onReceiveMessage(
                        TextMessage.FriendTextMessage(
                            msgId = msgID,
                            msg = text,
                            timestamp = V2TIMManager.getInstance().serverTime,
                            sender = convertGroupMember(sender)
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
        val id = chat.id
        val count = 40
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                val callback = object : V2TIMValueCallback<List<V2TIMMessage>> {
                    override fun onSuccess(t: List<V2TIMMessage>) {
                        continuation.resume(
                            LoadMessageResult.Success(
                                messageList = convertMessage(t),
                                loadFinish = t.size < count
                            )
                        )
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(LoadMessageResult.Failed(reason = "code: $code desc: $desc"))
                    }
                }
                when (chat) {
                    is Chat.C2C -> {
                        V2TIMManager.getMessageManager().getC2CHistoryMessageList(
                            id,
                            count,
                            lastMessage?.tag as? V2TIMMessage,
                            callback
                        )
                    }
                    is Chat.Group -> {
                        V2TIMManager.getMessageManager().getGroupHistoryMessageList(
                            id,
                            count,
                            lastMessage?.tag as? V2TIMMessage,
                            callback
                        )
                    }
                }
            }
        }
    }

    override suspend fun send(chat: Chat, text: String, channel: Channel<Message>) {
        val id = chat.id
        val messageId = RandomUtils.generateMessageId()
        val sendingMessage = TextMessage.SelfTextMessage(
            msgId = messageId, state = MessageState.Sending,
            timestamp = V2TIMManager.getInstance().serverTime, msg = text,
            sender = AppConst.personProfile.value,
        )
        channel.send(sendingMessage)
        val callback = object : V2TIMValueCallback<V2TIMMessage> {
            override fun onSuccess(t: V2TIMMessage) {
                coroutineScope.launch(Dispatchers.Main) {
                    val msg = convertMessage(t) as? TextMessage
                    if (msg == null) {
                        channel.send(sendingMessage.copy(state = MessageState.SendFailed))
                    } else {
                        channel.send(msg)
//                        channel.send(sendingMessage.copy(state = MessageState.Completed))
                    }
                    channel.close()
                }
            }

            override fun onError(code: Int, desc: String?) {
                coroutineScope.launch(Dispatchers.Main) {
                    channel.send(
                        sendingMessage.copy(state = MessageState.SendFailed).apply {
                            tag = "code: $code desc: $desc"
                        })
                    channel.close()
                }
            }
        }
        when (chat) {
            is Chat.C2C -> {
                V2TIMManager.getInstance().sendC2CTextMessage(text, id, callback)
            }
            is Chat.Group -> {
                V2TIMManager.getInstance()
                    .sendGroupTextMessage(text, id, V2TIMMessage.V2TIM_PRIORITY_HIGH, callback)
            }
        }
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

}