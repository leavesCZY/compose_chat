package github.leavesc.compose_chat.base.model

import github.leavesc.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesC
 * @Date: 2021/6/20 23:40
 * @Desc:
 * @Github：https://github.com/leavesC
 */
sealed class MessageState {

    object Sending : MessageState()

    object SendFailed : MessageState()

    object Completed : MessageState()

}

data class MessageDetail(
    val msgId: String,
    val timestamp: Long,
    val state: MessageState,
    val sender: PersonProfile,
    val isSelfMessage: Boolean
) {

    val conversationTime by lazy {
        TimeUtil.toConversationTime(timestamp)
    }

    val chatTime by lazy {
        TimeUtil.toChatTime(timestamp)
    }

    var tag: Any? = null

}

sealed class Message(val messageDetail: MessageDetail) {

    abstract val formatMessage: String

}

data class TimeMessage(
    val targetMessage: Message
) : Message(
    messageDetail = MessageDetail(
        msgId = (targetMessage.messageDetail.timestamp + targetMessage.messageDetail.msgId.hashCode()).toString(),
        timestamp = targetMessage.messageDetail.timestamp,
        state = MessageState.Completed,
        sender = PersonProfile.Empty,
        isSelfMessage = false
    )
) {

    override val formatMessage
        get() = throw IllegalAccessException()

}

data class TextMessage(
    val detail: MessageDetail,
    val msg: String,
) : Message(messageDetail = detail) {

    override val formatMessage = msg

}

data class ImageMessage(
    val detail: MessageDetail,
    val imagePath: String,
) : Message(messageDetail = detail) {

    override val formatMessage = "[图片]"

}