package github.leavesczy.compose_chat.ui.main.friendship.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.base.models.PersonProfile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList

@Stable
data class FriendshipPageViewState(
    val listState: LazyListState,
    val joinedGroupList: PersistentList<GroupProfile>,
    val friendList: PersistentList<PersonProfile>,
    val onClickGroupItem: (group: GroupProfile) -> Unit,
    val onClickFriendItem: (friend: PersonProfile) -> Unit,
    val onClickShowFriendshipDialog: () -> Unit
)

@Stable
data class FriendshipDialogViewState(
    val isVisible: Boolean,
    val groupIds: ImmutableList<GroupId>,
    val onDismissDialog: () -> Unit,
    val onJoinGroup: (groupId: String) -> Unit,
    val onAddFriend: (userId: String) -> Unit
)

@Stable
data class GroupId(
    val id: String,
    val name: String
)