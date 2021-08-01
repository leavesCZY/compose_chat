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
        when (lastMessage) {
            is TimeMessage -> {
                "error"
            }
            is TextMessage -> {
                if (lastMessage.state == MessageState.SendFailed) {
                    "[发送失败] " + lastMessage.msg
                } else {
                    lastMessage.msg
                }
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
    ) : Conversation(userID, name, faceUrl, unreadMessageCount, lastMessage, isPinned)

    class GroupConversation(
        groupId: String,
        name: String,
        faceUrl: String,
        unreadMessageCount: Int,
        lastMessage: Message,
        isPinned: Boolean
    ) : Conversation(groupId, name, faceUrl, unreadMessageCount, lastMessage, isPinned)

    override fun toString(): String {
        return "Conversation(id='$id', name='$name', faceUrl='$faceUrl', unreadMessageCount=$unreadMessageCount, lastMessage=$lastMessage, isPinned=$isPinned)"
    }

}