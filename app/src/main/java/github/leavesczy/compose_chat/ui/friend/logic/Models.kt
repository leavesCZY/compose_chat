package github.leavesczy.compose_chat.ui.friend.logic

import github.leavesczy.compose_chat.base.model.PersonProfile

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
data class FriendProfilePageViewState(
    val personProfile: PersonProfile,
    val itIsMe: Boolean,
    val isFriend: Boolean,
    val showSetFriendRemarkPanel: () -> Unit,
    val addFriend: () -> Unit
)

data class SetFriendRemarkDialogViewState(
    val visible: Boolean,
    val personProfile: PersonProfile,
    val dismissSetFriendRemarkDialog: () -> Unit,
    val setFriendRemark: (String) -> Unit,
)