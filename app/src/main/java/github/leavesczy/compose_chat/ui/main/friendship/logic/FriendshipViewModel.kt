package github.leavesczy.compose_chat.ui.main.friendship.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.ui.chat.main.ChatActivity
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.main.person.logic.PersonProfileViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
abstract class FriendshipViewModel : PersonProfileViewModel() {

    private val groupProvider: IGroupProvider = ComposeChat.groupProvider

    var friendshipPageViewState by mutableStateOf(
        value = FriendshipPageViewState(
            listState = LazyListState(
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0
            ),
            joinedGroupList = persistentListOf(),
            friendList = persistentListOf(),
            onClickGroupItem = ::onClickGroupItem,
            onClickFriendItem = ::onClickFriendItem,
            onClickShowFriendshipDialog = ::showFriendshipDialog
        )
    )
        private set

    var friendshipDialogViewState by mutableStateOf(
        value = FriendshipDialogViewState(
            isVisible = false,
            groupIds = persistentListOf(),
            onDismissDialog = ::dismissFriendshipDialog,
            onJoinGroup = ::joinGroup,
            onAddFriend = ::addFriend
        )
    )
        private set

    init {
        viewModelScope.launch {
            launch {
                groupProvider.joinedGroupListFlow.collect { joinedGroupList ->
                    friendshipPageViewState =
                        friendshipPageViewState.copy(joinedGroupList = joinedGroupList.toPersistentList())
                }
            }
            launch {
                friendshipProvider.friendListFlow.collect { friendList ->
                    friendshipPageViewState =
                        friendshipPageViewState.copy(friendList = friendList.toPersistentList())
                }
            }
        }
        groupProvider.refreshJoinedGroupList()
        friendshipProvider.refreshFriendList()
    }

    private fun onClickGroupItem(groupProfile: GroupProfile) {
        ChatActivity.navTo(
            context = context,
            chat = Chat.Group(id = groupProfile.id)
        )
    }

    private fun onClickFriendItem(personProfile: PersonProfile) {
        FriendProfileActivity.navTo(
            context = context,
            friendId = personProfile.id
        )
    }

    override fun showFriendshipDialog() {
        val ids = listOf(
            "@TGS#3SSMB3WHI",
            "@TGS#3VOZA3WHT",
            "@TGS#3W42A3WHP",
            "@TGS#3DMJIK6MS",
            "@TGS#3YCNIK6MC"
        )
        val groupIds = ids.mapIndexed { index, id ->
            GroupId(
                id = id,
                name = getString(resId = R.string.join_group_template, index + 1)
            )
        }.toImmutableList()
        friendshipDialogViewState = friendshipDialogViewState.copy(
            isVisible = true,
            groupIds = groupIds
        )
    }

    private fun dismissFriendshipDialog() {
        friendshipDialogViewState = friendshipDialogViewState.copy(isVisible = false)
    }

    private fun addFriend(userId: String) {
        viewModelScope.launch {
            val formatUserId = userId.lowercase()
            when (val result = friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400L)
                    showToast(resId = R.string.toast_add_friend_success)
                    ChatActivity.navTo(
                        context = context,
                        chat = Chat.C2C(id = formatUserId)
                    )
                    dismissFriendshipDialog()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }
            }
        }
    }

    private fun joinGroup(groupId: String) {
        viewModelScope.launch {
            showLoadingDialog()
            when (val result = groupProvider.joinGroup(groupId = groupId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 800L)
                    showToast(resId = R.string.toast_join_group_success)
                    groupProvider.refreshJoinedGroupList()
                    dismissFriendshipDialog()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }
            }
            dismissLoadingDialog()
        }
    }

}