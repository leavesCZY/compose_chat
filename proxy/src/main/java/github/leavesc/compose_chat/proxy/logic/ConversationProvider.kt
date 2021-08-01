package github.leavesc.compose_chat.proxy.logic

import com.tencent.imsdk.v2.*
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.Conversation
import github.leavesc.compose_chat.base.provider.IConversationProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resume

/**
 * @Author: leavesC
 * @Date: 2021/6/21 21:19
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class ConversationProvider : IConversationProvider, Converters {

    override val conversationList = MutableStateFlow<List<Conversation>>(emptyList())

    override val totalUnreadCount = MutableStateFlow(0L)

    init {
        V2TIMManager.getConversationManager()
            .setConversationListener(object : V2TIMConversationListener() {
                override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>) {
                    getConversationList()
                }

                override fun onNewConversation(conversationList: MutableList<V2TIMConversation>?) {
                    getConversationList()
                }

                override fun onTotalUnreadMessageCountChanged(totalUnreadCount: Long) {
                    this@ConversationProvider.totalUnreadCount.value = totalUnreadCount
                }
            })
    }

    override fun getConversationList() {
        GlobalScope.launch(Dispatchers.Main) {
            dispatchConversationList(getConversationListOrigin())
        }
    }

    override suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getConversationManager()
                    .pinConversation(getConversationId(conversation), pin, object : V2TIMCallback {
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

    override suspend fun deleteConversation(conversation: Conversation): ActionResult {
        return deleteConversation(getConversationId(conversation))
    }

    private fun dispatchConversationList(conversationList: List<Conversation>) {
        this.conversationList.value = conversationList
    }

    private suspend fun getConversationListOrigin(): List<Conversation> {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                V2TIMManager.getConversationManager().getConversationList(0, 50, object :
                    V2TIMValueCallback<V2TIMConversationResult> {
                    override fun onSuccess(result: V2TIMConversationResult) {
                        continuation.resume(convertConversation(result.conversationList))
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(emptyList())
                    }
                })
            }
        }
    }

    private fun convertConversation(convertersList: List<V2TIMConversation>?): List<Conversation> {
        return convertersList?.mapNotNull { convertConversation(it) }?.sortedByDescending {
            if (it.isPinned) {
                it.lastMessage.timestamp.toDouble() + Long.MAX_VALUE
            } else {
                it.lastMessage.timestamp.toDouble()
            }
        } ?: emptyList()
    }

    private fun convertConversation(conversation: V2TIMConversation): Conversation? {
        return when (conversation.type) {
            V2TIMConversation.V2TIM_C2C -> {
                val lastMessage = convertMessage(conversation.lastMessage) ?: return null
                return Conversation.C2CConversation(
                    userID = conversation.userID ?: "",
                    name = conversation.showName ?: "",
                    faceUrl = conversation.faceUrl ?: "",
                    unreadMessageCount = conversation.unreadCount,
                    lastMessage = lastMessage,
                    isPinned = conversation.isPinned
                )
            }
            V2TIMConversation.V2TIM_GROUP -> {
                val lastMessage = convertMessage(conversation.lastMessage) ?: return null
                return Conversation.GroupConversation(
                    groupId = conversation.groupID ?: "",
                    name = conversation.showName ?: "",
                    faceUrl = conversation.faceUrl ?: "",
                    unreadMessageCount = conversation.unreadCount,
                    lastMessage = lastMessage,
                    isPinned = conversation.isPinned
                )
            }
            else -> {
                null
            }
        }
    }

}