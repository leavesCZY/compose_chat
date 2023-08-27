package github.leavesczy.compose_chat.ui.logic

import android.content.Intent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.ServerState
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.provider.ContextProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipDialogViewState
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.profile.ProfileUpdateActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainViewModel : BaseViewModel() {

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    var topBarViewState by mutableStateOf(
        value = MainPageTopBarViewState(
            openDrawer = ::openDrawer,
            showFriendshipDialog = ::showFriendshipDialog
        )
    )
        private set

    var bottomBarViewState by mutableStateOf(
        value = MainPageBottomBarViewState(
            selectedTab = MainPageTab.Conversation,
            unreadMessageCount = 0,
            onClickTab = ::onClickTab
        )
    )
        private set

    var drawerViewState by mutableStateOf(
        value = MainPageDrawerViewState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            personProfile = ComposeChat.accountProvider.personProfile.value,
            previewImage = ::previewImage,
            switchTheme = ::switchTheme,
            logout = ::logout,
            updateProfile = ::updateProfile
        )
    )
        private set

    var friendshipDialogViewState by mutableStateOf(
        FriendshipDialogViewState(
            visible = false,
            onDismissRequest = ::onFriendshipDialogDismissRequest,
            joinGroup = ::joinGroup,
            addFriend = ::addFriend
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

    private fun onClickTab(mainPageTab: MainPageTab) {
        if (bottomBarViewState.selectedTab != mainPageTab) {
            bottomBarViewState = bottomBarViewState.copy(selectedTab = mainPageTab)
        }
    }

    private fun addFriend(userId: String) {
        viewModelScope.launch {
            val formatUserId = userId.lowercase()
            when (val result = ComposeChat.friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    showToast(msg = "添加成功")
                    ChatActivity.navTo(
                        context = ContextProvider.context,
                        chat = Chat.PrivateChat(id = formatUserId)
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

    private fun logout() {
        viewModelScope.launch {
            loadingDialog(visible = true)
            when (val result = ComposeChat.accountProvider.logout()) {
                is ActionResult.Success -> {
                    ComposeChat.conversationProvider.clear()
                    ComposeChat.groupProvider.clear()
                    ComposeChat.friendshipProvider.clear()
                    AccountProvider.onUserLogout()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
            loadingDialog(visible = false)
        }
    }

    private suspend fun openDrawer() {
        drawerViewState.drawerState.open()
    }

    private fun showFriendshipDialog() {
        friendshipDialogViewState = friendshipDialogViewState.copy(visible = true)
    }

    private fun onFriendshipDialogDismissRequest() {
        friendshipDialogViewState = friendshipDialogViewState.copy(visible = false)
    }

    private fun updateProfile() {
        val intent = Intent(context, ProfileUpdateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun previewImage(imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            PreviewImageActivity.navTo(context = context, imageUrl = imageUrl)
        }
    }

    private fun switchTheme() {
        val nextTheme = AppThemeProvider.appTheme.nextTheme()
        AppThemeProvider.onAppThemeChanged(appTheme = nextTheme)
    }

    private fun AppTheme.nextTheme(): AppTheme {
        val values = AppTheme.values()
        return values.getOrElse(ordinal + 1) {
            values[0]
        }
    }

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }

}