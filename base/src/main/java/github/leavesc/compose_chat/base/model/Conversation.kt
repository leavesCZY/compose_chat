package github.leavesc.compose_chat.base.model

/**
 * @Author: leavesC
 * @Date: 2021/6/21 21:26
 * @Desc:
 * @Github：https://github.com/leavesC
 */
sealed class Conversation(
    val id: String,
    val name: String,
    val faceUrl: String,
    val unreadMessageCount: Int,
    val lastMessage: Message,
    val isPinned: Boolean
) {

    val formatMsg by lazy {
        val messageDetail = lastMessage.messageDetail
        val prefix =
            if (this is GroupConversation && !messageDetail.isSelfMessage) {
                messageDetail.sender.showName + " : "
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
            MessageState.SendFailed -> {
                "[发送失败] " + lastMessage.formatMessage
            }
        }
    }

    class C2CConversation(
        userID: String,
        name: String,
        faceUrl: String,
        unreadMessageCount: Int,
        lastMessage: Message,
        isPinned: Boolean
    ) : Conversation(
        id = userID,
        name = name,
        faceUrl = faceUrl,
        unreadMessageCount = unreadMessageCount,
        lastMessage = lastMessage,
        isPinned = isPinned
    )

    class GroupConversation(
        groupId: String,
        name: String,
        faceUrl: String,
        unreadMessageCount: Int,
        lastMessage: Message,
        isPinned: Boolean
    ) : Conversation(
        id = groupId,
        name = name,
        faceUrl = faceUrl,
        unreadMessageCount = unreadMessageCount,
        lastMessage = lastMessage,
        isPinned = isPinned
    )

    override fun toString(): String {
        return "Conversation(id='$id', name='$name', faceUrl='$faceUrl', unreadMessageCount=$unreadMessageCount, lastMessage=$lastMessage, isPinned=$isPinned, formatMsg='$formatMsg')"
    }

}