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
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.di.ComposeChat
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FriendshipViewModel : BaseViewModel() {

    private val friendshipProvider = ComposeChat.friendshipProvider

    private val groupProvider = ComposeChat.groupProvider

    private var isAddingFriend = false

    private var isJoiningGroup = false

    var pageViewState by mutableStateOf(
        value = FriendshipPageViewState(
            listState = LazyListState(
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0
            ),
            joinedGroupList = persistentListOf(),
            friendList = persistentListOf(),
            isLoading = false,
            loadError = null,
            onClickGroupItem = ::onClickGroupItem,
            onClickFriendItem = ::onClickFriendItem,
            onClickShowFriendshipDialog = ::showFriendshipDialog,
            onRetryLoad = ::retryLoad
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

    private var lastServerConnectState = ServerConnectState.Idle

    init {
        viewModelScope.launch {
            launch {
                groupProvider.joinedGroupListFlow.collect { joinedGroupList ->
                    pageViewState =
                        pageViewState.copy(joinedGroupList = joinedGroupList.toPersistentList())
                }
            }
            launch {
                friendshipProvider.friendListFlow.collect { friendList ->
                    pageViewState =
                        pageViewState.copy(friendList = friendList.toPersistentList())
                }
            }
            launch {
                groupProvider.joinedGroupListLoadingFlow.collect { groupLoading ->
                    refreshLoadingState(groupLoading = groupLoading)
                }
            }
            launch {
                friendshipProvider.friendListLoadingFlow.collect { friendLoading ->
                    refreshLoadingState(friendLoading = friendLoading)
                }
            }
            launch {
                groupProvider.joinedGroupListLoadErrorFlow.collect { groupError ->
                    refreshErrorState(groupError = groupError)
                }
            }
            launch {
                friendshipProvider.friendListLoadErrorFlow.collect { friendError ->
                    refreshErrorState(friendError = friendError)
                }
            }
            launch {
                ComposeChat.accountProvider.serverConnectStateFlow.collect { serverConnectState ->
                    if (serverConnectState == ServerConnectState.Connected &&
                        lastServerConnectState != ServerConnectState.Connected &&
                        lastServerConnectState != ServerConnectState.Idle
                    ) {
                        refreshLists()
                    }
                    lastServerConnectState = serverConnectState
                }
            }
        }
        refreshLists()
    }

    private fun refreshLoadingState(
        groupLoading: Boolean = groupProvider.joinedGroupListLoadingFlow.value,
        friendLoading: Boolean = friendshipProvider.friendListLoadingFlow.value
    ) {
        pageViewState = pageViewState.copy(
            isLoading = groupLoading || friendLoading
        )
    }

    private fun refreshErrorState(
        groupError: String? = groupProvider.joinedGroupListLoadErrorFlow.value,
        friendError: String? = friendshipProvider.friendListLoadErrorFlow.value
    ) {
        val messages = buildList {
            if (!groupError.isNullOrBlank()) {
                add(getString(resId = R.string.toast_group_load_failed, groupError))
            }
            if (!friendError.isNullOrBlank()) {
                add(getString(resId = R.string.toast_friendship_load_failed, friendError))
            }
        }
        pageViewState = pageViewState.copy(
            loadError = messages.joinToString(separator = "\n").ifBlank { null }
        )
    }

    private fun refreshLists() {
        groupProvider.refreshJoinedGroupList()
        friendshipProvider.refreshFriendList()
    }

    private fun retryLoad() {
        refreshLists()
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

    fun showFriendshipDialog() {
        val groupIds = ComposeChat.demoSampleGroupIds.mapIndexed { index, id ->
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
        if (isAddingFriend) {
            return
        }
        viewModelScope.launch {
            isAddingFriend = true
            try {
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
            } finally {
                isAddingFriend = false
            }
        }
    }

    private fun joinGroup(groupId: String) {
        if (isJoiningGroup) {
            return
        }
        viewModelScope.launch {
            isJoiningGroup = true
            showLoadingDialog()
            try {
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
            } finally {
                dismissLoadingDialog()
                isJoiningGroup = false
            }
        }
    }

}
