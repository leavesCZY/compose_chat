package github.leavesczy.compose_chat.common.provider

import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.common.model.Conversation
import kotlinx.coroutines.flow.SharedFlow

/**
 * @Author: leavesCZY
 * @Date: 2021/6/22 12:01
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IConversationProvider {

    val conversationList: SharedFlow<List<Conversation>>

    val totalUnreadMessageCount: SharedFlow<Long>

    fun getConversationList()

    fun getTotalUnreadMessageCount()

    suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult

    suspend fun deleteC2CConversation(userId: String): ActionResult

    suspend fun deleteGroupConversation(groupId: String): ActionResult

}