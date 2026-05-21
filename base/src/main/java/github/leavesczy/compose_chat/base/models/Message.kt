package github.leavesczy.compose_chat.base.models

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.R
import github.leavesczy.compose_chat.base.utils.StringResources
import github.leavesczy.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Stable
sealed class MessageState {

    @Stable
    data object Sending : MessageState()

    @Stable
    data class Failed(val reason: String) : MessageState()

    @Stable
    data object Success : MessageState()

}

@Stable
data class MessageDetail(
    val msgId: String,
    val milliseconds: Long,
    val state: MessageState,
    val sender: PersonProfile,
    val isOwnMessage: Boolean
) {

    val conversationTime = TimeUtil.formatConversationTime(milliseconds = milliseconds)

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
data class ImageMessage(
    private val messageDetail: MessageDetail,
    private val original: ImageElement,
    private val large: ImageElement?,
    private val thumb: ImageElement?,
) : Message(detail = messageDetail) {

    @Stable
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
data class TimeMessage(val targetMessage: Message) : Message(
    detail = MessageDetail(
        msgId = (targetMessage.detail.milliseconds + targetMessage.detail.msgId.hashCode()).toString(),
        milliseconds = targetMessage.detail.milliseconds,
        state = MessageState.Success,
        sender = PersonProfile.Empty,
        isOwnMessage = false
    )
) {

    override val formatMessage = TimeUtil.formatMessageTime(milliseconds = detail.milliseconds)

}

@Stable
sealed class LoadMessageResult {

    @Stable
    data class Success(
        val messageList: List<Message>,
        val loadFinish: Boolean
    ) : LoadMessageResult()

    @Stable
    data class Failed(val reason: String) : LoadMessageResult()

}