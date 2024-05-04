package github.leavesczy.compose_chat.ui.friendship.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.base.models.PersonProfile

@Stable
data class FriendshipPageViewState(
    val listState: MutableState<LazyListState>,
    val joinedGroupList: MutableState<List<GroupProfile>>,
    val friendList: MutableState<List<PersonProfile>>,
    val onClickGroupItem: (GroupProfile) -> Unit,
    val onClickFriendItem: (PersonProfile) -> Unit,
    val showFriendshipDialog: () -> Unit
)

@Stable
data class FriendshipDialogViewState(
    val visible: MutableState<Boolean>,
    val dismissDialog: () -> Unit,
    val joinGroup: (groupId: String) -> Unit,
    val addFriend: (userId: String) -> Unit
)