package github.leavesczy.compose_chat.base.model

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
sealed class Conversation(
    val id: String,
    val name: String,
    val faceUrl: String,
    val unreadMessageCount: Int,
    val lastMessage: Message,
    val isPinned: Boolean
) {

    val formatMsg by lazy(mode = LazyThreadSafetyMode.NONE) {
        val messageDetail = lastMessage.messageDetail
        val prefix =
            if (this is GroupConversation && lastMessage !is SystemMessage && !messageDetail.isSelfMessage) {
                messageDetail.sender.showName + "："
            } else {
                ""
            }
        prefix + when (messageDetail.state) {
            MessageState.Completed -> {
                lastMessage.formatMessage
            }

            MessageState.Sending -> {
                "[发送中] " + lastMessage.formatMessage
            }

            is MessageState.SendFailed -> {
                "[发送失败] " + lastMessage.formatMessage
            }
        }
    }

}

class C2CConversation(
    id: String,
    name: String,
    faceUrl: String,
    unreadMessageCount: Int,
    lastMessage: Message,
    isPinned: Boolean
) : Conversation(
    id = id,
    name = name,
    faceUrl = faceUrl,
    unreadMessageCount = unreadMessageCount,
    lastMessage = lastMessage,
    isPinned = isPinned
)

class GroupConversation(
    id: String,
    name: String,
    faceUrl: String,
    unreadMessageCount: Int,
    lastMessage: Message,
    isPinned: Boolean
) : Conversation(
    id = id,
    name = name,
    faceUrl = faceUrl,
    unreadMessageCount = unreadMessageCount,
    lastMessage = lastMessage,
    isPinned = isPinned
)