package github.leavesczy.compose_chat.proxy

import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMConversationListener
import com.tencent.imsdk.v2.V2TIMConversationResult
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ConversationType
import github.leavesczy.compose_chat.base.provider.IConversationProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object ConversationProvider : IConversationProvider {

    override val conversationListFlow = MutableSharedFlow<List<Conversation>>()

    override val totalUnreadMessageCountFlow = MutableSharedFlow<Long>()

    private val conversationComparator = Comparator<Conversation> { o1, o2 ->
        val o1Timestamp = o1.lastMessage.detail.milliseconds
        val o2Timestamp = o2.lastMessage.detail.milliseconds
        when {
            o1.isPinned && o2.isPinned -> o2Timestamp.compareTo(o1Timestamp)
            o1.isPinned -> -1
            o2.isPinned -> 1
            else -> o2Timestamp.compareTo(o1Timestamp)
        }
    }

    private var refreshJob: Job? = null

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
                    AppCoroutineScope.launch {
                        totalUnreadMessageCountFlow.emit(value = totalUnreadCount)
                    }
                }
            }
        )
    }

    override fun refreshConversationList() {
        refreshJob?.cancel()
        refreshJob = AppCoroutineScope.launch {
            val conversationList = getConversationListOrigin()
            dispatchConversationList(conversationList = conversationList)
        }
    }

    override fun refreshTotalUnreadMessageCount() {
        V2TIMManager.getConversationManager()
            .getTotalUnreadMessageCount(object : V2TIMValueCallback<Long> {
                override fun onSuccess(totalUnreadCount: Long) {
                    AppCoroutineScope.launch {
                        totalUnreadMessageCountFlow.emit(value = totalUnreadCount)
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    AppCoroutineScope.launch {
                        totalUnreadMessageCountFlow.emit(value = 0)
                    }
                }
            })
    }

    override suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getConversationManager().pinConversation(
                Converters.getConversationKey(conversation = conversation),
                pin,
                object : V2TIMCallback {
                    override fun onSuccess() {
                        continuation.resume(value = ActionResult.Success)
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(
                            value = ActionResult.Failed(
                                code = code,
                                reason = desc
                            )
                        )
                    }
                }
            )
        }
    }

    override suspend fun deleteC2CConversation(userId: String): ActionResult {
        return Converters.deleteC2CConversation(userId = userId)
    }

    override suspend fun deleteGroupConversation(groupId: String): ActionResult {
        return Converters.deleteGroupConversation(groupId = groupId)
    }

    private suspend fun dispatchConversationList(conversationList: List<Conversation>) {
        this@ConversationProvider.conversationListFlow.emit(value = conversationList)
    }

    private suspend fun getConversationListOrigin(): List<Conversation> {
        var nextStep = 0L
        val conversationList = mutableListOf<Conversation>()
        while (true) {
            val pair = getConversationList(nextStep = nextStep)
            conversationList.addAll(elements = pair.first)
            nextStep = pair.second
            if (nextStep <= 0) {
                break
            }
        }
        return conversationList
    }

    private suspend fun getConversationList(nextStep: Long): Pair<List<Conversation>, Long> {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getConversationManager().getConversationList(
                nextStep,
                100,
                object : V2TIMValueCallback<V2TIMConversationResult> {
                    override fun onSuccess(result: V2TIMConversationResult) {
                        val convertersList = result.conversationList.filter { conversation ->
                            !conversation.userID.isNullOrBlank() || !conversation.groupID.isNullOrBlank()
                        }
                        continuation.resume(
                            value = Pair(
                                first = convertConversation(convertersList = convertersList),
                                second = if (result.isFinished) {
                                    0
                                } else {
                                    result.nextSeq
                                }
                            )
                        )
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume(value = Pair(first = emptyList(), second = 0))
                    }
                }
            )
        }
    }

    private fun convertConversation(convertersList: List<V2TIMConversation>?): List<Conversation> {
        return convertersList?.mapNotNull { conversation ->
            convertConversation(conversation = conversation)
        }?.sortedWith(conversationComparator) ?: emptyList()
    }

    private fun convertConversation(conversation: V2TIMConversation): Conversation? {
        val lastConversationMessage = conversation.lastMessage ?: return null
        val name = conversation.showName?.trim() ?: ""
        val avatarUrl = conversation.faceUrl ?: ""
        val unreadMessageCount = conversation.unreadCount.toLong()
        val lastMessage = Converters.convertMessage(timMessage = lastConversationMessage)
        val isPinned = conversation.isPinned
        return when (conversation.type) {
            V2TIMConversation.V2TIM_C2C -> {
                Conversation(
                    id = conversation.userID ?: "",
                    name = name,
                    avatarUrl = avatarUrl,
                    unreadMessageCount = unreadMessageCount,
                    lastMessage = lastMessage,
                    isPinned = isPinned,
                    type = ConversationType.C2C
                )
            }

            V2TIMConversation.V2TIM_GROUP -> {
                Conversation(
                    id = conversation.groupID ?: "",
                    name = name,
                    avatarUrl = avatarUrl,
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