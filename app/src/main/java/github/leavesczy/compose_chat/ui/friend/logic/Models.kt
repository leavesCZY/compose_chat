package github.leavesczy.compose_chat.ui.friend.logic

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.PersonProfile

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class FriendProfilePageViewState(
    val personProfile: MutableState<PersonProfile?>,
    val itIsMe: MutableState<Boolean>,
    val isFriend: MutableState<Boolean>,
    val showSetFriendRemarkPanel: () -> Unit,
    val addFriend: () -> Unit
)

@Stable
data class SetFriendRemarkDialogViewState(
    val visible: MutableState<Boolean>,
    val remark: MutableState<String>,
    val setFriendRemark: (String) -> Unit,
    val dismissDialog: () -> Unit
)