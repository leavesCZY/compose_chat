package github.leavesczy.compose_chat.ui.friendship.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.proxy.FriendshipProvider
import github.leavesczy.compose_chat.proxy.GroupProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendshipViewModel : BaseViewModel() {

    private val friendshipProvider: IFriendshipProvider = FriendshipProvider()

    private val groupProvider: IGroupProvider = GroupProvider()

    var pageViewState by mutableStateOf(
        value = FriendshipPageViewState(
            listState = LazyListState(
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0
            ),
            joinedGroupList = persistentListOf(),
            friendList = persistentListOf(),
            onClickGroupItem = ::onClickGroupItem,
            onClickFriendItem = ::onClickFriendItem,
            showFriendshipDialog = ::showFriendshipDialog
        )
    )
        private set

    var friendshipDialogViewState by mutableStateOf(
        FriendshipDialogViewState(
            visible = false,
            dismissDialog = ::dismissFriendshipDialog,
            joinGroup = ::joinGroup,
            addFriend = ::addFriend
        )
    )
        private set

    init {
        viewModelScope.launch {
            launch {
                groupProvider.joinedGroupList.collect {
                    pageViewState = pageViewState.copy(joinedGroupList = it.toPersistentList())
                }
            }
            launch {
                friendshipProvider.friendList.collect {
                    pageViewState = pageViewState.copy(friendList = it.toPersistentList())
                }
            }
        }
        groupProvider.refreshJoinedGroupList()
        friendshipProvider.refreshFriendList()
    }

    private fun onClickGroupItem(groupProfile: GroupProfile) {
        ChatActivity.navTo(
            context = context,
            chat = Chat.GroupChat(id = groupProfile.id)
        )
    }

    private fun onClickFriendItem(personProfile: PersonProfile) {
        FriendProfileActivity.navTo(
            context = context,
            friendId = personProfile.id
        )
    }

    fun showFriendshipDialog() {
        friendshipDialogViewState = friendshipDialogViewState.copy(visible = true)
    }

    private fun dismissFriendshipDialog() {
        friendshipDialogViewState = friendshipDialogViewState.copy(visible = false)
    }

    private fun addFriend(userId: String) {
        viewModelScope.launch {
            val formatUserId = userId.lowercase()
            when (val result = friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    showToast(msg = "添加成功")
                    ChatActivity.navTo(
                        context = context,
                        chat = Chat.PrivateChat(id = formatUserId)
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
            when (val result = groupProvider.joinGroup(groupId = groupId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 800)
                    showToast(msg = "加入成功")
                    groupProvider.refreshJoinedGroupList()
                    dismissFriendshipDialog()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }
            }
        }
    }

}