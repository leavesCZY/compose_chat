package github.leavesczy.compose_chat.base.models

import github.leavesczy.compose_chat.base.R
import github.leavesczy.compose_chat.base.utils.StringResources
import github.leavesczy.compose_chat.base.utils.TimeUtils

sealed class MessageState {

    data object Sending : MessageState()

    data class Failed(val reason: String) : MessageState()

    data object Success : MessageState()

}

data class MessageDetail(
    val msgId: String,
    val milliseconds: Long,
    val state: MessageState,
    val sender: PersonProfile,
    val isOwnMessage: Boolean
) {

    val conversationTime = TimeUtils.formatConversationTime(milliseconds = milliseconds)

}

sealed class Message(val detail: MessageDetail) {

    abstract val formatMessage: String

}

data class TextMessage(
    private val messageDetail: MessageDetail,
    private val text: String
) : Message(detail = messageDetail) {

    override val formatMessage: String
        get() = text

}

data class ImageMessage(
    private val messageDetail: MessageDetail,
    private val original: ImageElement,
    private val large: ImageElement?,
    private val thumb: ImageElement?
) : Message(detail = messageDetail) {

    data class ImageElement(
        val width: Int,
        val height: Int,
        val url: String
    )

    override val formatMessage: String
        get() = StringResources.getString(resId = R.string.message_image)

    val previewImage: ImageElement
        get() = large ?: original

    val previewImageUrl: String
        get() = previewImage.url

    val isLocalPreview: Boolean
        get() {
            val url = previewImageUrl
            return url.isNotBlank() &&
                    !url.startsWith(prefix = "http://") &&
                    !url.startsWith(prefix = "https://")
        }

    fun withPreservedPreview(messageDetail: MessageDetail): ImageMessage {
        return copy(messageDetail = messageDetail)
    }

}

data class SystemMessage(
    private val messageDetail: MessageDetail,
    private val tips: String
) : Message(detail = messageDetail) {

    override val formatMessage: String
        get() = tips

}

data class TimeMessage(val targetMessage: Message) : Message(
    detail = MessageDetail(
        msgId = (targetMessage.detail.milliseconds + targetMessage.detail.msgId.hashCode()).toString(),
        milliseconds = targetMessage.detail.milliseconds,
        state = MessageState.Success,
        sender = PersonProfile.Empty,
        isOwnMessage = false
    )
) {

    override val formatMessage = TimeUtils.formatMessageTime(milliseconds = detail.milliseconds)

}

sealed class LoadMessageResult {

    data class Success(
        val messageList: List<Message>,
        val isLoadFinished: Boolean
    ) : LoadMessageResult()

    data class Failed(val reason: String) : LoadMessageResult()

}
