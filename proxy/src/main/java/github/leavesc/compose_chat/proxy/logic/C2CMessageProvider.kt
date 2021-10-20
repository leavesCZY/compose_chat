package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.*
import github.leavesc.compose_chat.base.provider.IC2CMessageProvider
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
class C2CMessageProvider : IC2CMessageProvider, Converters {

    private val listenerMap =
        mutableMapOf<String, SoftReference<IC2CMessageProvider.MessageListener>>()

    init {
        V2TIMManager.getInstance().addSimpleMsgListener(object : V2TIMSimpleMsgListener() {
            override fun onRecvC2CTextMessage(
                msgID: String,
                sender: V2TIMUserInfo,
                text: String
            ) {
                val friendId = sender.userID
                val softListener = listenerMap[friendId]
                if (softListener != null) {
                    val listener = softListener.get()
                    if (listener != null) {
                        val senderProfile = PersonProfile(
                            userId = friendId,
                            faceUrl = sender.faceUrl ?: "",
                            nickname = sender.nickName ?: "",
                            remark = "",
                            signature = ""
                        )
                        listener.onReceiveMessage(
                            TextMessage.FriendTextMessage(
                                msgId = msgID,
                                msg = text,
                                timestamp = V2TIMManager.getInstance().serverTime,
                                sender = senderProfile
                            )
                        )
                    } else {
                        listenerMap.remove(friendId)
                    }
                }
            }
        })
    }

    override fun markC2CMessageAsRead(friendId: String) {
        V2TIMManager.getMessageManager().markC2CMessageAsRead(friendId, null)
    }

    override suspend fun getHistoryMessageList(
        friendId: String,
        lastMessage: Message?
    ): LoadMessageResult {
        return withContext(Dispatchers.Main) {
            val count = 40
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getMessageManager()
                    .getC2CHistoryMessageList(friendId, count, lastMessage?.tag as? V2TIMMessage,
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

    private fun convertMessage(messageList: List<V2TIMMessage>?): List<Message> {
        return messageList?.mapNotNull { convertMessage(it) } ?: emptyList()
    }

    override suspend fun send(channel: Channel<Message>, friendId: String, text: String) {
        val messageId = RandomUtils.generateMessageId()
        val sendingMessage = TextMessage.SelfTextMessage(
            msgId = messageId, state = MessageState.Sending,
            timestamp = V2TIMManager.getInstance().serverTime, msg = text,
            sender = AppConst.personProfile.value,
        )
        channel.send(sendingMessage)
        V2TIMManager.getInstance()
            .sendC2CTextMessage(text, friendId, object : V2TIMValueCallback<V2TIMMessage> {
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
                        channel.send(sendingMessage.copy(state = MessageState.SendFailed).apply {
                            tag = "code: $code desc: $desc"
                        })
                        channel.close()
                    }
                }
            })
    }

    override fun startReceive(
        friendId: String,
        messageListener: IC2CMessageProvider.MessageListener
    ) {
        listenerMap.remove(friendId)
        listenerMap[friendId] = SoftReference(messageListener)
        checkListener()
    }

    override fun stopReceive(messageListener: IC2CMessageProvider.MessageListener) {
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