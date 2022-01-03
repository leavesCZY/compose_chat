package github.leavesc.compose_chat.base.provider

import github.leavesc.compose_chat.base.model.Chat
import github.leavesc.compose_chat.base.model.LoadMessageResult
import github.leavesc.compose_chat.base.model.Message
import kotlinx.coroutines.channels.Channel

/**
 * @Author: leavesC
 * @Date: 2021/6/22 11:02
 * @Desc:
 * @Github：https://github.com/leavesC
 */
interface IMessageProvider {

    interface MessageListener {

        fun onReceiveMessage(message: Message)

    }

    fun startReceive(chat: Chat, messageListener: MessageListener)

    fun stopReceive(messageListener: MessageListener)

    suspend fun sendText(
        chat: Chat,
        text: String,
        messageChannel: Channel<Message>
    )

    suspend fun sendImage(
        chat: Chat,
        imagePath: String,
        messageChannel: Channel<Message>
    )

    suspend fun uploadImage(
        chat: Chat,
        imagePath: String
    ): String

    suspend fun getHistoryMessage(chat: Chat, lastMessage: Message?): LoadMessageResult

    fun markMessageAsRead(chat: Chat)

}