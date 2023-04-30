package github.leavesczy.compose_chat.ui.main.logic

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.ServerState
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.utils.ContextHolder
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainViewModel : ViewModel() {

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    var bottomBarViewState by mutableStateOf(
        value = MainPageBottomBarViewState(
            selectedTab = MainTab.Conversation,
            unreadMessageCount = 0
        )
    )
        private set

    var drawerViewState by mutableStateOf(
        value = MainPageDrawerViewState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            personProfile = ComposeChat.accountProvider.personProfile.value
        )
    )
        private set

    var friendshipDialogViewState by mutableStateOf(
        FriendshipDialogViewState(
            visible = false,
            onDismissRequest = ::onFriendshipDialogDismissRequest,
            joinGroup = ::joinGroup,
            addFriend = ::addFriend,
        )
    )
        private set

    private val _serverConnectState = MutableStateFlow(value = ServerState.ConnectSuccess)

    val serverConnectState: SharedFlow<ServerState> = _serverConnectState

    init {
        viewModelScope.launch {
            launch {
                ComposeChat.conversationProvider.totalUnreadMessageCount.collect {
                    bottomBarViewState = bottomBarViewState.copy(unreadMessageCount = it)
                }
            }
            launch {
                ComposeChat.accountProvider.personProfile.collect {
                    drawerViewState = drawerViewState.copy(personProfile = it)
                }
            }
            launch {
                ComposeChat.accountProvider.serverConnectState.collect {
                    _serverConnectState.emit(value = it)
                    if (it == ServerState.ConnectSuccess) {
                        requestData()
                    }
                }
            }
            launch {
                requestData()
            }
        }
    }

    private fun requestData() {
        ComposeChat.conversationProvider.refreshConversationList()
        ComposeChat.conversationProvider.refreshTotalUnreadMessageCount()
        ComposeChat.friendshipProvider.refreshFriendList()
        ComposeChat.groupProvider.refreshJoinedGroupList()
        ComposeChat.accountProvider.refreshPersonProfile()
    }

    fun onTabSelected(tab: MainTab) {
        bottomBarViewState = bottomBarViewState.copy(selectedTab = tab)
    }

    fun showFriendshipDialog() {
        friendshipDialogViewState = friendshipDialogViewState.copy(visible = true)
    }

    private fun onFriendshipDialogDismissRequest() {
        friendshipDialogViewState = friendshipDialogViewState.copy(visible = false)
    }

    private fun addFriend(userId: String) {
        viewModelScope.launch {
            val formatUserId = userId.lowercase()
            when (val result = ComposeChat.friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    showToast(msg = "添加成功")
                    ChatActivity.navTo(
                        context = ContextHolder.context, chat = Chat.PrivateChat(id = formatUserId)
                    )
                    onFriendshipDialogDismissRequest()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

    private fun joinGroup(groupId: String) {
        viewModelScope.launch {
            when (val result = ComposeChat.groupProvider.joinGroup(groupId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    showToast(msg = "加入成功")
                    ComposeChat.groupProvider.refreshJoinedGroupList()
                    onFriendshipDialogDismissRequest()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            showLoadingDialog(visible = true)
            when (val result = ComposeChat.accountProvider.logout()) {
                is ActionResult.Success -> {
                    AccountProvider.onUserLogout()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
            showLoadingDialog(visible = false)
        }
    }

    fun switchToNextTheme() {
        val nextTheme = AppThemeProvider.appTheme.nextTheme()
        AppThemeProvider.onAppThemeChanged(appTheme = nextTheme)
    }

    private fun showLoadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }

}