package github.leavesczy.compose_chat.proxy

import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMConversationListener
import com.tencent.imsdk.v2.V2TIMConversationResult
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.ConversationType
import github.leavesczy.compose_chat.base.models.MessageDetail
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.provider.IConversationProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

object ConversationProvider : IConversationProvider {

    private val _conversationListFlow = MutableStateFlow<List<Conversation>>(value = emptyList())

    override val conversationListFlow: StateFlow<List<Conversation>> =
        _conversationListFlow.asStateFlow()

    private val _totalUnreadMessageCountFlow = MutableStateFlow(value = 0L)

    override val totalUnreadMessageCountFlow: StateFlow<Long> =
        _totalUnreadMessageCountFlow.asStateFlow()

    private val _conversationLoadErrorFlow = MutableStateFlow<String?>(value = null)

    override val conversationLoadErrorFlow: StateFlow<String?> =
        _conversationLoadErrorFlow.asStateFlow()

    private val _conversationLoadingFlow = MutableStateFlow(value = false)

    override val conversationLoadingFlow: StateFlow<Boolean> =
        _conversationLoadingFlow.asStateFlow()

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

    private val updateMutex = Mutex()

    private var refreshJob: Job? = null

    private var mergeJob: Job? = null

    private var removeJob: Job? = null

    @Volatile
    private var refreshGeneration = 0

    @Volatile
    private var listEpoch = 0

    init {
        V2TIMManager.getConversationManager().addConversationListener(
            object : V2TIMConversationListener() {
                override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>) {
                    mergeConversationList(conversationList = conversationList)
                }

                override fun onNewConversation(conversationList: MutableList<V2TIMConversation>?) {
                    if (!conversationList.isNullOrEmpty()) {
                        mergeConversationList(conversationList = conversationList)
                    }
                }

                override fun onConversationDeleted(conversationIDList: MutableList<String>?) {
                    if (!conversationIDList.isNullOrEmpty()) {
                        removeConversations(conversationKeys = conversationIDList)
                    }
                }

                override fun onTotalUnreadMessageCountChanged(totalUnreadCount: Long) {
                    _totalUnreadMessageCountFlow.value = totalUnreadCount
                }
            }
        )
    }

    override fun refreshConversationList() {
        refreshJob?.cancel()
        mergeJob?.cancel()
        removeJob?.cancel()
        listEpoch++
        val generation = ++refreshGeneration
        refreshJob = AppCoroutineScope.launch {
            _conversationLoadingFlow.value = _conversationListFlow.value.isEmpty()
            try {
                when (val result = getConversationListOrigin()) {
                    is ConversationListFetchResult.Success -> {
                        if (generation != refreshGeneration) {
                            return@launch
                        }
                        updateMutex.withLock {
                            if (generation != refreshGeneration) {
                                return@withLock
                            }
                            listEpoch++
                            _conversationLoadErrorFlow.value = null
                            _conversationListFlow.value = result.conversationList
                        }
                    }

                    is ConversationListFetchResult.Failed -> {
                        if (generation != refreshGeneration) {
                            return@launch
                        }
                        updateMutex.withLock {
                            if (generation != refreshGeneration) {
                                return@withLock
                            }
                            _conversationLoadErrorFlow.value = result.reason
                            if (_conversationListFlow.value.isEmpty()) {
                                _conversationListFlow.value = emptyList()
                            }
                        }
                    }
                }
            } finally {
                if (generation == refreshGeneration) {
                    _conversationLoadingFlow.value = false
                }
            }
        }
    }

    override fun refreshTotalUnreadMessageCount() {
        V2TIMManager.getConversationManager()
            .getTotalUnreadMessageCount(object : V2TIMValueCallback<Long> {
                override fun onSuccess(totalUnreadCount: Long) {
                    _totalUnreadMessageCountFlow.value = totalUnreadCount
                }

                override fun onError(code: Int, desc: String?) {
                    // Keep the last known unread count instead of pretending everything is read.
                }
            })
    }

    override suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult {
        return suspendTimCallback { callback ->
            V2TIMManager.getConversationManager().pinConversation(
                Converters.getConversationKey(conversation = conversation),
                pin,
                callback
            )
        }
    }

    override suspend fun deleteC2CConversation(userId: String): ActionResult {
        return Converters.deleteC2CConversation(userId = userId)
    }

    override suspend fun deleteGroupConversation(groupId: String): ActionResult {
        return Converters.deleteGroupConversation(groupId = groupId)
    }

    override fun clearSessionState() {
        refreshJob?.cancel()
        mergeJob?.cancel()
        removeJob?.cancel()
        refreshGeneration++
        listEpoch++
        _conversationListFlow.value = emptyList()
        _totalUnreadMessageCountFlow.value = 0L
        _conversationLoadErrorFlow.value = null
        _conversationLoadingFlow.value = false
    }

    private fun mergeConversationList(conversationList: List<V2TIMConversation>) {
        if (refreshJob?.isActive == true) {
            return
        }
        mergeJob?.cancel()
        val epoch = listEpoch
        mergeJob = AppCoroutineScope.launch {
            val converted = convertConversation(convertersList = conversationList)
                .filter { conversation -> conversation.id.isNotBlank() }
            if (converted.isEmpty()) {
                return@launch
            }
            updateMutex.withLock {
                if (epoch != listEpoch) {
                    return@withLock
                }
                val merged = _conversationListFlow.value
                    .associateBy { conversation ->
                        conversationKey(conversation = conversation)
                    }
                    .toMutableMap()
                converted.forEach { conversation ->
                    merged[conversationKey(conversation = conversation)] = conversation
                }
                _conversationLoadErrorFlow.value = null
                _conversationListFlow.value =
                    merged.values.sortedWith(comparator = conversationComparator)
            }
        }
    }

    private fun removeConversations(conversationKeys: List<String>) {
        if (refreshJob?.isActive == true) {
            return
        }
        mergeJob?.cancel()
        removeJob?.cancel()
        val epoch = listEpoch
        removeJob = AppCoroutineScope.launch {
            updateMutex.withLock {
                if (epoch != listEpoch) {
                    return@withLock
                }
                val keySet = conversationKeys.toHashSet()
                _conversationListFlow.value =
                    _conversationListFlow.value.filterNot { conversation ->
                        keySet.contains(
                            element = Converters.getConversationKey(conversation = conversation)
                        )
                    }
            }
        }
    }

    private fun conversationKey(conversation: Conversation): String {
        return Converters.getConversationKey(conversation = conversation)
    }

    private suspend fun getConversationListOrigin(): ConversationListFetchResult {
        var nextStep = 0L
        val conversationList = mutableListOf<Conversation>()
        while (true) {
            when (val page = getConversationList(nextStep = nextStep)) {
                is ConversationPageFetchResult.Success -> {
                    conversationList.addAll(elements = page.conversationList)
                    nextStep = page.nextSeq
                    if (nextStep <= 0) {
                        break
                    }
                }

                is ConversationPageFetchResult.Failed -> {
                    return ConversationListFetchResult.Failed(reason = page.reason)
                }
            }
        }
        return ConversationListFetchResult.Success(
            conversationList = conversationList.sortedWith(comparator = conversationComparator)
        )
    }

    private suspend fun getConversationList(nextStep: Long): ConversationPageFetchResult {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getConversationManager().getConversationList(
                nextStep,
                100,
                object : V2TIMValueCallback<V2TIMConversationResult> {
                    override fun onSuccess(result: V2TIMConversationResult) {
                        if (!continuation.isActive) {
                            return
                        }
                        val convertersList = result.conversationList.filter { conversation ->
                            !conversation.userID.isNullOrBlank() || !conversation.groupID.isNullOrBlank()
                        }
                        continuation.resume(
                            value = ConversationPageFetchResult.Success(
                                conversationList = convertConversation(convertersList = convertersList),
                                nextSeq = if (result.isFinished) {
                                    0
                                } else {
                                    result.nextSeq
                                }
                            )
                        )
                    }

                    override fun onError(code: Int, desc: String?) {
                        if (!continuation.isActive) {
                            return
                        }
                        continuation.resume(
                            value = ConversationPageFetchResult.Failed(
                                reason = "$code ${desc.orEmpty()}"
                            )
                        )
                    }
                }
            )
        }
    }

    private fun convertConversation(convertersList: List<V2TIMConversation>?): List<Conversation> {
        return convertersList?.mapNotNull { conversation ->
            convertConversation(conversation = conversation)
        } ?: emptyList()
    }

    private fun convertConversation(conversation: V2TIMConversation): Conversation? {
        val name = conversation.showName?.trim() ?: ""
        val avatarUrl = conversation.faceUrl ?: ""
        val unreadMessageCount = conversation.unreadCount.toLong()
        val lastMessage = conversation.lastMessage?.let { lastConversationMessage ->
            Converters.convertMessage(timMessage = lastConversationMessage)
        } ?: emptyConversationMessage()
        val isPinned = conversation.isPinned
        return when (conversation.type) {
            V2TIMConversation.V2TIM_C2C -> {
                val id = conversation.userID ?: ""
                if (id.isBlank()) {
                    null
                } else {
                    Conversation(
                        id = id,
                        name = name,
                        avatarUrl = avatarUrl,
                        unreadMessageCount = unreadMessageCount,
                        lastMessage = lastMessage,
                        isPinned = isPinned,
                        type = ConversationType.C2C
                    )
                }
            }

            V2TIMConversation.V2TIM_GROUP -> {
                val id = conversation.groupID ?: ""
                if (id.isBlank()) {
                    null
                } else {
                    Conversation(
                        id = id,
                        name = name,
                        avatarUrl = avatarUrl,
                        unreadMessageCount = unreadMessageCount,
                        lastMessage = lastMessage,
                        isPinned = isPinned,
                        type = ConversationType.Group
                    )
                }
            }

            else -> {
                null
            }
        }
    }

    private fun emptyConversationMessage(): TextMessage {
        return TextMessage(
            messageDetail = MessageDetail(
                msgId = "conversation_empty_${System.nanoTime()}",
                milliseconds = 0L,
                state = MessageState.Success,
                sender = PersonProfile.Empty,
                isOwnMessage = false
            ),
            text = ""
        )
    }

    private sealed class ConversationListFetchResult {
        data class Success(val conversationList: List<Conversation>) : ConversationListFetchResult()
        data class Failed(val reason: String) : ConversationListFetchResult()
    }

    private sealed class ConversationPageFetchResult {
        data class Success(
            val conversationList: List<Conversation>,
            val nextSeq: Long
        ) : ConversationPageFetchResult()

        data class Failed(val reason: String) : ConversationPageFetchResult()
    }

}
