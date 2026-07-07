package github.leavesczy.compose_chat.ui.friend.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

@Stable
data class FriendProfilePageViewState(
    val personProfile: PersonProfile?,
    val isMe: Boolean,
    val isFriend: Boolean,
    val onClickSetFriendRemark: () -> Unit,
    val onClickAddFriend: () -> Unit,
    val onClickDeleteFriend: () -> Unit,
    val onClickChat: () -> Unit
)

@Stable
data class DeleteFriendDialogViewState(
    val isVisible: Boolean,
    val onDismissDialog: () -> Unit,
    val onDeleteFriend: () -> Unit
)

@Stable
data class SetFriendRemarkDialogViewState(
    val isVisible: Boolean,
    val remark: String,
    val onSetFriendRemark: (remark: String) -> Unit,
    val onDismissDialog: () -> Unit
)