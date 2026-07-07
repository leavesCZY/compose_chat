package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Conversation
import kotlinx.coroutines.flow.SharedFlow

interface IConversationProvider {

    val conversationListFlow: SharedFlow<List<Conversation>>

    val totalUnreadMessageCountFlow: SharedFlow<Long>

    fun refreshConversationList()

    fun refreshTotalUnreadMessageCount()

    suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult

    suspend fun deleteC2CConversation(userId: String): ActionResult

    suspend fun deleteGroupConversation(groupId: String): ActionResult

}