package github.leavesczy.compose_chat.ui.friendship.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendshipViewModel : BaseViewModel() {

    var pageViewState by mutableStateOf(
        value = FriendshipPageViewState(
            listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
            joinedGroupList = emptyList(),
            friendList = emptyList(),
            onClickGroupItem = ::onClickGroupItem,
            onClickFriendItem = ::onClickFriendItem
        )
    )
        private set

    init {
        viewModelScope.launch {
            launch {
                ComposeChat.groupProvider.joinedGroupList.collect {
                    pageViewState = pageViewState.copy(joinedGroupList = it)
                }
            }
            launch {
                ComposeChat.friendshipProvider.friendList.collect {
                    pageViewState = pageViewState.copy(friendList = it)
                }
            }
        }
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

}