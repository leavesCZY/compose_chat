package github.leavesczy.compose_chat.base.models

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.R
import github.leavesczy.compose_chat.base.utils.StringResources

@Stable
data class Conversation(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val unreadMessageCount: Long,
    val lastMessage: Message,
    val isPinned: Boolean,
    val type: ConversationType
) {

    val formatMessage = run {
        val messageDetail = lastMessage.detail
        val senderName = when (type) {
            ConversationType.C2C -> {
                ""
            }

            ConversationType.Group -> {
                when (lastMessage) {
                    is TextMessage,
                    is ImageMessage -> {
                        if (messageDetail.isOwnMessage) {
                            ""
                        } else {
                            messageDetail.sender.showName + ": "
                        }
                    }

                    is SystemMessage,
                    is TimeMessage -> {
                        ""
                    }
                }
            }
        }
        val messageState = when (messageDetail.state) {
            MessageState.Success -> {
                ""
            }

            MessageState.Sending -> {
                StringResources.getString(resId = R.string.message_sending) + " "
            }

            is MessageState.Failed -> {
                StringResources.getString(resId = R.string.message_send_failed) + " "
            }
        }
        senderName + messageState + lastMessage.formatMessage
    }

}

@Stable
enum class ConversationType {
    C2C,
    Group;
}