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
interface IC2CMessageProvider {

    interface MessageListener {

        fun onReceiveMessage(message: Message)

    }

    fun markC2CMessageAsRead(friendId: String)

    suspend fun getHistoryMessageList(friendId: String, lastMessage: Message?): LoadMessageResult

    suspend fun send(
        channel: Channel<Message>,
        friendId: String,
        text: String
    )

    fun startReceive(friendId: String, messageListener: MessageListener)

    fun stopReceive(messageListener: MessageListener)

}