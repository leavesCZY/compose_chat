package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.Conversation
import kotlinx.coroutines.flow.SharedFlow

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IConversationProvider {

    val conversationList: SharedFlow<List<Conversation>>

    val totalUnreadMessageCount: SharedFlow<Long>

    fun refreshConversationList()

    fun refreshTotalUnreadMessageCount()

    fun cleanConversationUnreadMessageCount(chat: Chat)

    suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult

    suspend fun deleteC2CConversation(userId: String): ActionResult

    suspend fun deleteGroupConversation(groupId: String): ActionResult

}