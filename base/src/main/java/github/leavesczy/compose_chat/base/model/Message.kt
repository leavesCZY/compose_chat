package github.leavesczy.compose_chat.base.model

import github.leavesczy.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesCZY
 * @Date: 2021/6/20 23:40
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
sealed class MessageState {

    object Sending : MessageState()

    class SendFailed(val failReason: String) : MessageState()

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

}

sealed class Message(val messageDetail: MessageDetail) {

    abstract val formatMessage: String

    var tag: Any? = null

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

data class ImageElement(val width: Int, val height: Int, val url: String)

data class ImageMessage(
    val detail: MessageDetail,
    val original: ImageElement,
    val large: ImageElement?,
    val thumb: ImageElement?,
) : Message(messageDetail = detail) {

    override val formatMessage = "[图片]"

}