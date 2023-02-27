package github.leavesczy.compose_chat.ui.chat.logic

import androidx.compose.foundation.lazy.LazyListState
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.GroupMemberProfile
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.base.model.Message

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
data class ChatPageViewState(
    val chat: Chat,
    val topBarTitle: String,
    val listState: LazyListState,
    val messageList: List<Message>
)

data class LoadMessageViewState(
    val refreshing: Boolean,
    val loadFinish: Boolean
)

data class ChatPageAction(
    val onClickAvatar: (Message) -> Unit,
    val onClickMessage: (Message) -> Unit,
    val onLongClickMessage: (Message) -> Unit
)

data class GroupProfilePageViewState(
    val groupProfile: GroupProfile,
    val memberList: List<GroupMemberProfile>
)

data class GroupProfilePageAction(
    val setAvatar: (String) -> Unit,
    val quitGroup: () -> Unit,
    val onClickMember: (GroupMemberProfile) -> Unit
)