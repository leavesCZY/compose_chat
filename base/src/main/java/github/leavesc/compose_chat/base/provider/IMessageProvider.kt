package github.leavesc.compose_chat.base.provider

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

    fun markMessageAsRead(partyId: String)

    suspend fun getHistoryMessage(partyId: String, lastMessage: Message?): LoadMessageResult

    suspend fun send(
        channel: Channel<Message>,
        partyId: String,
        text: String
    )

    fun startReceive(partyId: String, messageListener: MessageListener)

    fun stopReceive(messageListener: MessageListener)

}