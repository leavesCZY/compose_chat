package github.leavesczy.compose_chat.ui.friendship.logic

import github.leavesczy.compose_chat.base.model.PersonProfile

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
data class SetFriendRemarkDialogViewState(
    val visible: Boolean,
    val personProfile: PersonProfile
)

enum class FriendProfilePageAction {
    NavToChat,
    FinishActivity

}