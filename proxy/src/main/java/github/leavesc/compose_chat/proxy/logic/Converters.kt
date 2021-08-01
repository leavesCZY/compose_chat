package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import github.leavesc.compose_chat.base.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * @Author: leavesC
 * @Date: 2021/6/22 14:44
 * @Desc:
 * @Github：https://github.com/leavesC
 */
internal interface Converters {

    suspend fun deleteConversation(id: String): ActionResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getConversationManager().deleteConversation(
                    id, object : V2TIMCallback {
                        override fun onSuccess() {
                            continuation.resume(ActionResult.Success)
                        }

                        override fun onError(code: Int, desc: String?) {
                            continuation.resume(ActionResult.Failed(desc ?: ""))
                        }
                    })
            }
        }
    }

    suspend fun deleteC2CConversation(userId: String): ActionResult {
        return deleteConversation(String.format("c2c_%s", userId))
    }

    suspend fun deleteGroupConversation(groupId: String): ActionResult {
        return deleteConversation(String.format("group_%s", groupId))
    }

    fun getConversationId(conversation: Conversation): String {
        return when (conversation) {
            is Conversation.C2CConversation -> {
                String.format("c2c_%s", conversation.id)
            }
            is Conversation.GroupConversation -> {
                String.format("group_%s", conversation.id)
            }
        }
    }

    fun convertMessage(timMessage: V2TIMMessage?): Message? {
        val messageType = timMessage?.message?.messageType
        if (messageType == com.tencent.imsdk.message.Message.MESSAGE_TYPE_C2C
            || messageType == com.tencent.imsdk.message.Message.MESSAGE_TYPE_GROUP
        ) {
            return when (timMessage.elemType) {
                V2TIMMessage.V2TIM_ELEM_TYPE_TEXT -> {
                    val senderProfile = PersonProfile(
                        userId = timMessage.sender,
                        faceUrl = timMessage.faceUrl ?: "",
                        nickname = timMessage.nickName ?: "",
                        remark = timMessage.friendRemark ?: "",
                        signature = ""
                    )
                    if (timMessage.isSelf) {
                        TextMessage.SelfTextMessage(
                            msgId = timMessage.msgID ?: "",
                            msg = timMessage.textElem?.text ?: "",
                            state = convertMessageState(timMessage.status),
                            timestamp = timMessage.timestamp,
                            sender = senderProfile
                        )
                    } else {
                        TextMessage.FriendTextMessage(
                            msgId = timMessage.msgID ?: "",
                            msg = timMessage.textElem?.text ?: "",
                            timestamp = timMessage.timestamp,
                            sender = senderProfile
                        )
                    }
                }
                else -> {
                    null
                }
            }?.apply {
                tag = timMessage
            }
        }
        return null
    }

    private fun convertMessageState(state: Int): MessageState {
        return when (state) {
            V2TIMMessage.V2TIM_MSG_STATUS_SENDING -> {
                MessageState.Sending
            }
            V2TIMMessage.V2TIM_MSG_STATUS_SEND_SUCC -> {
                MessageState.Completed
            }
            V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL -> {
                MessageState.SendFailed
            }
            else -> {
                MessageState.Completed
            }
        }
    }

}