package github.leavesc.compose_chat.base.provider

import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.Conversation
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesC
 * @Date: 2021/6/22 12:01
 * @Desc:
 * @Github：https://github.com/leavesC
 */
interface IConversationProvider {

    val conversationList: StateFlow<List<Conversation>>

    val totalUnreadCount: StateFlow<Long>

    fun getConversationList()

    suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult

    suspend fun deleteConversation(conversation: Conversation): ActionResult

}