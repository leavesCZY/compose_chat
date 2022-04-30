package github.leavesczy.compose_chat.common.provider

import github.leavesczy.compose_chat.common.model.Chat
import github.leavesczy.compose_chat.common.model.LoadMessageResult
import github.leavesczy.compose_chat.common.model.Message
import kotlinx.coroutines.channels.Channel

/**
 * @Author: leavesCZY
 * @Date: 2021/6/22 11:02
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IMessageProvider {

    interface MessageListener {

        fun onReceiveMessage(message: Message)

    }

    fun startReceive(chat: Chat, messageListener: MessageListener)

    fun stopReceive(messageListener: MessageListener)

    suspend fun sendText(
        chat: Chat,
        text: String
    ): Channel<Message>

    suspend fun sendImage(
        chat: Chat,
        imagePath: String
    ): Channel<Message>

    suspend fun uploadImage(
        chat: Chat,
        imagePath: String
    ): String

    suspend fun getHistoryMessage(chat: Chat, lastMessage: Message?): LoadMessageResult

    fun markMessageAsRead(chat: Chat)

}