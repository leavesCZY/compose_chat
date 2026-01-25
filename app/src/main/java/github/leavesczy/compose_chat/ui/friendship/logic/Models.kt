package github.leavesczy.compose_chat.ui.friendship.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.base.models.PersonProfile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList

/**
 * @Author: leavesCZY
 * @Date: 2026/1/23 21:27
 * @Desc:
 */
@Stable
data class FriendshipPageViewState(
    val listState: LazyListState,
    val joinedGroupList: PersistentList<GroupProfile>,
    val friendList: PersistentList<PersonProfile>,
    val onClickGroupItem: (GroupProfile) -> Unit,
    val onClickFriendItem: (PersonProfile) -> Unit,
    val showFriendshipDialog: () -> Unit
)

@Stable
data class FriendshipDialogViewState(
    val visible: Boolean,
    val groupIds: ImmutableList<GroupId>,
    val dismissDialog: () -> Unit,
    val joinGroup: (groupId: String) -> Unit,
    val addFriend: (userId: String) -> Unit
)

@Stable
data class GroupId(
    val id: String,
    val name: String
)