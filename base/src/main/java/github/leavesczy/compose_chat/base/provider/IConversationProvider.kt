package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.Conversation
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesCZY
 * @Date: 2021/6/22 12:01
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IConversationProvider {

    val conversationList: StateFlow<List<Conversation>>

    val totalUnreadMessageCount: StateFlow<Long>

    fun getConversationList()

    fun getTotalUnreadMessageCount()

    suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult

    suspend fun deleteC2CConversation(userId: String): ActionResult

    suspend fun deleteGroupConversation(groupId: String): ActionResult

}