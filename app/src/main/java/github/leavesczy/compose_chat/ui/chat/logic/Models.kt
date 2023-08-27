package github.leavesczy.compose_chat.ui.chat.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.GroupMemberProfile
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.base.model.Message

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class ChatPageViewState(
    val chat: Chat,
    val listState: LazyListState,
    val topBarTitle: String,
    val messageList: List<Message>
)

@Stable
data class LoadMessageViewState(
    val refreshing: Boolean,
    val loadFinish: Boolean
)

@Stable
data class ChatPageAction(
    val onClickAvatar: (Message) -> Unit,
    val onClickMessage: (Message) -> Unit,
    val onLongClickMessage: (Message) -> Unit
)

@Stable
data class GroupProfilePageViewState(
    val groupProfile: GroupProfile,
    val memberList: List<GroupMemberProfile>
)

@Stable
data class GroupProfilePageAction(
    val setAvatar: (String) -> Unit,
    val quitGroup: () -> Unit,
    val onClickMember: (GroupMemberProfile) -> Unit
)