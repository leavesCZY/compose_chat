package github.leavesczy.compose_chat.base.models

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
sealed class MessageState {

    data object Sending : MessageState()

    data class SendFailed(val reason: String) : MessageState()

    data object Completed : MessageState()

}

@Stable
data class MessageDetail(
    val msgId: String,
    val timestamp: Long,
    val state: MessageState,
    val sender: PersonProfile,
    val isOwnMessage: Boolean
) {

    val conversationTime by lazy(mode = LazyThreadSafetyMode.NONE) {
        TimeUtil.toConversationTime(timeStamp = timestamp)
    }

    val chatTime by lazy(mode = LazyThreadSafetyMode.NONE) {
        TimeUtil.toChatTime(timeStamp = timestamp)
    }

}

@Stable
sealed class Message(val detail: MessageDetail) {

    abstract val formatMessage: String

    var tag: Any? = null

}

@Stable
data class TextMessage(
    private val messageDetail: MessageDetail,
    private val text: String
) : Message(detail = messageDetail) {

    override val formatMessage: String
        get() = text

}

@Stable
data class SystemMessage(
    private val messageDetail: MessageDetail,
    private val tips: String
) : Message(detail = messageDetail) {

    override val formatMessage: String
        get() = tips

}

@Stable
data class ImageMessage(
    private val messageDetail: MessageDetail,
    private val original: ImageElement,
    private val large: ImageElement?,
    private val thumb: ImageElement?,
) : Message(detail = messageDetail) {

    override val formatMessage = "[图片]"

    val previewImage: ImageElement
        get() = large ?: original

    val previewImageUrl: String
        get() = previewImage.url

}

@Stable
class ImageElement(
    val width: Int,
    val height: Int,
    val url: String
)

@Stable
class TimeMessage(targetMessage: Message) : Message(
    detail = MessageDetail(
        msgId = (targetMessage.detail.timestamp + targetMessage.detail.msgId.hashCode()).toString(),
        timestamp = targetMessage.detail.timestamp,
        state = MessageState.Completed,
        sender = PersonProfile.Empty,
        isOwnMessage = false
    )
) {

    override val formatMessage: String
        get() = detail.chatTime

}

@Stable
sealed class LoadMessageResult {

    data class Success(val messageList: List<Message>, val loadFinish: Boolean) :
        LoadMessageResult()

    data class Failed(val reason: String) : LoadMessageResult()

}