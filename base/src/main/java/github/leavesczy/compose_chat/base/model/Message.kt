package github.leavesczy.compose_chat.base.model

import github.leavesczy.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
sealed class MessageState {

    object Sending : MessageState()

    class SendFailed(val reason: String) : MessageState()

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

data class TextMessage(
    private val detail: MessageDetail, private val text: String
) : Message(
    messageDetail = detail
) {

    override val formatMessage = text

}

data class SystemMessage(
    private val detail: MessageDetail, private val tips: String
) : Message(
    messageDetail = detail
) {

    override val formatMessage = tips

}

data class ImageMessage(
    private val detail: MessageDetail,
    private val original: ImageElement,
    private val large: ImageElement?,
    private val thumb: ImageElement?,
) : Message(messageDetail = detail) {

    val preview = large ?: original

    val previewUrl = preview.url

    override val formatMessage = "[图片]"

    private val maxImageRatio = 1.9f

    val widgetWidthDp = 190f

    val widgetHeightDp = if (preview.width <= 0f || preview.height <= 0f) {
        widgetWidthDp
    } else {
        widgetWidthDp * (minOf(
            maxImageRatio, 1.0f * preview.height / preview.width
        ))
    }

}

class ImageElement(
    val width: Int, val height: Int, val url: String
)

class TimeMessage(
    targetMessage: Message
) : Message(
    messageDetail = MessageDetail(
        msgId = (targetMessage.messageDetail.timestamp + targetMessage.messageDetail.msgId.hashCode()).toString(),
        timestamp = targetMessage.messageDetail.timestamp,
        state = MessageState.Completed,
        sender = PersonProfile.Empty,
        isSelfMessage = false
    )
) {

    override val formatMessage = messageDetail.chatTime

}

sealed class LoadMessageResult {

    data class Success(
        val messageList: List<Message>, val loadFinish: Boolean
    ) : LoadMessageResult()

    data class Failed(val reason: String) : LoadMessageResult()

}