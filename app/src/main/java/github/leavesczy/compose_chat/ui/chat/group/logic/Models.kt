package github.leavesczy.compose_chat.ui.chat.group.logic

import android.app.Activity
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import kotlinx.collections.immutable.PersistentList

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
@Stable
data class GroupProfilePageViewState(
    val groupProfile: GroupProfile?,
    val memberList: PersistentList<GroupMemberProfile>,
    val onClickMember: (activity: Activity, member: GroupMemberProfile) -> Unit,
    val onClickSwitchAvatar: () -> Unit,
    val onClickQuitGroup: (activity: Activity) -> Unit
)