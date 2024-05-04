package github.leavesczy.compose_chat.proxy.logic

import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMConversationListener
import com.tencent.imsdk.v2.V2TIMConversationResult
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ConversationType
import github.leavesczy.compose_chat.base.provider.IConversationProvider
import github.leavesczy.compose_chat.proxy.coroutine.ChatCoroutineScope
import github.leavesczy.compose_chat.proxy.utils.Converters
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ConversationProvider : IConversationProvider {

    override val conversationList = MutableSharedFlow<List<Conversation>>()

    override val totalUnreadMessageCount = MutableSharedFlow<Long>()

    init {
        V2TIMManager.getConversationManager().addConversationListener(
            object : V2TIMConversationListener() {
                override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>) {
                    refreshConversationList()
                }

                override fun onNewConversation(conversationList: MutableList<V2TIMConversation>?) {
                    refreshConversationList()
                }

                override fun onTotalUnreadMessageCountChanged(totalUnreadCount: Long) {
                    ChatCoroutineScope.launch {
                        totalUnreadMessageCount.emit(value = totalUnreadCount)
                    }
                }
            }
        )
    }

    override fun refreshConversationList() {
        ChatCoroutineScope.launch {
            dispatchConversationList(conversationList = getConversationListOrigin())
        }
    }

    override fun refreshTotalUnreadMessageCount() {
        V2TIMManager.getConversationManager()
            .getTotalUnreadMessageCount(object : V2TIMValueCallback<Long> {
                override fun onSuccess(totalUnreadCount: Long) {
                    ChatCoroutineScope.launch {
                        totalUnreadMessageCount.emit(value = totalUnreadCount)
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    ChatCoroutineScope.launch {
                        totalUnreadMessageCount.emit(value = 0)
                    }
                }
            })
    }

    override fun cleanConversationUnreadMessageCount(chat: Chat) {
        V2TIMManager.getConversationManager().cleanConversationUnreadMessageCount(
            Converters.getConversationKey(chat = chat),
            0,
            0,
            null
        )
    }

    override suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getConversationManager().pinConversation(
                Converters.getConversationKey(conversation),
                pin,
                object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(value = ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(
                            value = ActionResult.Failed(
                                code = code,
                                reason = desc ?: ""
                            )
                        )
                    }
                }
            )
        }
    }

    override suspend fun deleteC2CConversation(userId: String): ActionResult {
        return Converters.deleteC2CConversation(userId)
    }

    override suspend fun deleteGroupConversation(groupId: String): ActionResult {
        return Converters.deleteGroupConversation(groupId)
    }

    private suspend fun dispatchConversationList(conversationList: List<Conversation>) {
        this@ConversationProvider.conversationList.emit(value = conversationList)
    }

    private suspend fun getConversationListOrigin(): List<Conversation> {
        var nextStep = 0L
        val conversationList = mutableListOf<Conversation>()
        while (true) {
            val pair = getConversationList(nextStep = nextStep)
            conversationList.addAll(pair.first)
            nextStep = pair.second
            if (nextStep <= 0) {
                break
            }
        }
        return conversationList
    }

    private suspend fun getConversationList(nextStep: Long): Pair<List<Conversation>, Long> {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getConversationManager().getConversationList(nextStep,
                100,
                object : V2TIMValueCallback<V2TIMConversationResult> {
                    override fun onSuccess(result: V2TIMConversationResult) {
                        continuation.resume(
                            value = Pair(
                                convertConversation(result.conversationList.filter { !it.userID.isNullOrBlank() || !it.groupID.isNullOrBlank() }),
                                if (result.isFinished) {
                                    0
                                } else {
                                    result.nextSeq
                                }
                            )
                        )
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = Pair(emptyList(), 0))
                    }
                }
            )
        }
    }

    private fun convertConversation(convertersList: List<V2TIMConversation>?): List<Conversation> {
        return convertersList?.mapNotNull {
            convertConversation(conversation = it)
        }?.sortedByDescending {
            if (it.isPinned) {
                it.lastMessage.detail.timestamp.toDouble() + Long.MAX_VALUE
            } else {
                it.lastMessage.detail.timestamp.toDouble()
            }
        } ?: emptyList()
    }

    private fun convertConversation(conversation: V2TIMConversation): Conversation? {
        val lastConversationMessage = conversation.lastMessage ?: return null
        val name = conversation.showName ?: ""
        val faceUrl = conversation.faceUrl ?: ""
        val unreadMessageCount = conversation.unreadCount
        val lastMessage = Converters.convertMessage(timMessage = lastConversationMessage)
        val isPinned = conversation.isPinned
        return when (conversation.type) {
            V2TIMConversation.V2TIM_C2C -> {
                return Conversation(
                    id = conversation.userID ?: "",
                    name = name,
                    faceUrl = faceUrl,
                    unreadMessageCount = unreadMessageCount,
                    lastMessage = lastMessage,
                    isPinned = isPinned,
                    type = ConversationType.C2C
                )
            }

            V2TIMConversation.V2TIM_GROUP -> {
                return Conversation(
                    id = conversation.groupID ?: "",
                    name = name,
                    faceUrl = faceUrl,
                    unreadMessageCount = unreadMessageCount,
                    lastMessage = lastMessage,
                    isPinned = isPinned,
                    type = ConversationType.Group
                )
            }

            else -> {
                null
            }
        }
    }

}