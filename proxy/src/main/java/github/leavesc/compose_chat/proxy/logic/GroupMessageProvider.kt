package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.LoadMessageResult
import github.leavesc.compose_chat.base.model.Message
import github.leavesc.compose_chat.base.model.MessageState
import github.leavesc.compose_chat.base.model.TextMessage
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
 * @Date: 2021/6/20 22:52
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class GroupMessageProvider : IMessageProvider, Converters {

    private val listenerMap =
        mutableMapOf<String, SoftReference<IMessageProvider.MessageListener>>()

    init {
        V2TIMManager.getInstance().addSimpleMsgListener(object : V2TIMSimpleMsgListener() {

            override fun onRecvGroupTextMessage(
                msgID: String,
                groupID: String,
                sender: V2TIMGroupMemberInfo,
                text: String
            ) {
                val senderId = sender.userID
                val listener = listenerMap[senderId]?.get()
                if (listener != null) {
                    val senderProfile = convertGroupMember(sender)
                    listener.onReceiveMessage(
                        TextMessage.FriendTextMessage(
                            msgId = msgID,
                            msg = text,
                            timestamp = V2TIMManager.getInstance().serverTime,
                            sender = senderProfile
                        )
                    )
                } else {
                    listenerMap.remove(senderId)
                }
            }
        })
    }

    override fun markMessageAsRead(partyId: String) {
        V2TIMManager.getMessageManager().markGroupMessageAsRead(partyId, null)
    }

    override suspend fun getHistoryMessage(
        partyId: String,
        lastMessage: Message?
    ): LoadMessageResult {
        return withContext(Dispatchers.Main) {
            val count = 40
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getMessageManager()
                    .getGroupHistoryMessageList(partyId, count, lastMessage?.tag as? V2TIMMessage,
                        object : V2TIMValueCallback<List<V2TIMMessage>> {
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
                        })
            }
        }
    }

    override suspend fun send(channel: Channel<Message>, partyId: String, text: String) {
        val messageId = RandomUtils.generateMessageId()
        val sendingMessage = TextMessage.SelfTextMessage(
            msgId = messageId, state = MessageState.Sending,
            timestamp = V2TIMManager.getInstance().serverTime, msg = text,
            sender = AppConst.personProfile.value,
        )
        channel.send(sendingMessage)
        V2TIMManager.getInstance()
            .sendGroupTextMessage(
                text,
                partyId,
                V2TIMMessage.V2TIM_PRIORITY_HIGH,
                object : V2TIMValueCallback<V2TIMMessage> {
                    override fun onSuccess(t: V2TIMMessage) {
                        coroutineScope.launch(Dispatchers.Main) {
                            val msg = convertMessage(t) as? TextMessage
                            if (msg == null) {
                                channel.send(sendingMessage.copy(state = MessageState.SendFailed))
                            } else {
//                            channel.send(msg)
                                channel.send(sendingMessage.copy(state = MessageState.Completed))
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
                })
    }

    override fun startReceive(
        partyId: String,
        messageListener: IMessageProvider.MessageListener
    ) {
        listenerMap.remove(partyId)
        listenerMap[partyId] = SoftReference(messageListener)
        checkListener()
    }

    override fun stopReceive(messageListener: IMessageProvider.MessageListener) {
        val filter = listenerMap.filter { it.value.get() == messageListener }
        for (entry in filter) {
            listenerMap.remove(entry.key)
        }
        checkListener()
    }

    private fun checkListener() {
        val filter = listenerMap.filter { it.value.get() == null }
        for (entry in filter) {
            listenerMap.remove(entry.key)
        }
    }

}