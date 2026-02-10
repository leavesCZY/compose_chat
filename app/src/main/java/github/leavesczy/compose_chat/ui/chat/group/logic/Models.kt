package github.leavesczy.compose_chat.ui.chat.group.logic

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import kotlinx.collections.immutable.PersistentList

/**
 * @Author: leavesCZY
 * @Date: 2026/1/23 21:21
 * @Desc:
 */
@Stable
data class GroupProfilePageViewState(
    val groupProfile: GroupProfile?,
    val memberList: PersistentList<GroupMemberProfile>
)

@Stable
data class GroupProfilePageAction(
    val setAvatar: (String) -> Unit,
    val quitGroup: () -> Unit,
    val onClickMember: (GroupMemberProfile) -> Unit
)