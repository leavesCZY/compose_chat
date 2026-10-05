package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Conversation
import kotlinx.coroutines.flow.StateFlow

interface IConversationProvider {

    val conversationListFlow: StateFlow<List<Conversation>>

    val totalUnreadMessageCountFlow: StateFlow<Long>

    val conversationLoadErrorFlow: StateFlow<String?>

    val conversationLoadingFlow: StateFlow<Boolean>

    fun refreshConversationList()

    fun refreshTotalUnreadMessageCount()

    fun clearSessionState()

    suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult

    suspend fun deleteC2CConversation(userId: String): ActionResult

    suspend fun deleteGroupConversation(groupId: String): ActionResult

}