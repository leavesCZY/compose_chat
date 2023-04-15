package github.leavesczy.compose_chat.ui.main.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendshipViewModel : ViewModel() {

    var friendshipPageViewState by mutableStateOf(
        value = FriendshipPageViewState(
            listState = LazyListState(
                firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0
            ), joinedGroupList = emptyList(), friendList = emptyList()
        )
    )
        private set

    init {
        viewModelScope.launch {
            launch {
                ComposeChat.groupProvider.joinedGroupList.collect {
                    friendshipPageViewState = friendshipPageViewState.copy(joinedGroupList = it)
                }
            }
            launch {
                ComposeChat.friendshipProvider.friendList.collect {
                    friendshipPageViewState = friendshipPageViewState.copy(friendList = it)
                }
            }
        }
    }

}