package github.leavesczy.compose_chat.ui.main.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.cache.AccountCache
import github.leavesczy.compose_chat.cache.AppThemeCache
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.model.*
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.utils.ContextHolder
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/6/27 14:24
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainViewModel : ViewModel() {

    private val _conversationPageViewState = MutableStateFlow(
        value = ConversationPageViewState(
            listState = LazyListState(
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0
            ),
            conversationList = emptyList()
        )
    )

    private val _friendshipPageViewState = MutableStateFlow(
        value = FriendshipPageViewState(
            listState = LazyListState(
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0
            ),
            joinedGroupList = emptyList(),
            friendList = emptyList()
        )
    )

    private val _personProfilePageViewState =
        MutableStateFlow(value = PersonProfilePageViewState(personProfile = PersonProfile.Empty))

    private val _mainPageBottomBarViewState = MutableStateFlow(
        value = MainPageBottomBarViewState(
            tabList = MainTab.values().toList(),
            tabSelected = MainTab.Conversation,
            unreadMessageCount = 0
        )
    )

    private val _appTheme = MutableStateFlow(value = AppThemeCache.currentTheme)

    private val _serverConnectState = MutableStateFlow(value = ServerState.ConnectSuccess)

    private val _mainPageDrawerViewState = MutableStateFlow(
        value = MainPageDrawerViewState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            appTheme = _appTheme.value,
            personProfile = ComposeChat.accountProvider.personProfile.value
        )
    )

    private val _friendshipDialogViewState = MutableStateFlow(
        FriendshipDialogViewState(
            visible = false,
            onDismissRequest = ::onFriendshipPanelDismissRequest,
            joinGroup = ::joinGroup,
            addFriend = ::addFriend,
        )
    )

    val serverConnectState: SharedFlow<ServerState> = _serverConnectState

    val mainPageDrawerViewState: StateFlow<MainPageDrawerViewState> = _mainPageDrawerViewState

    val friendshipDialogViewState: StateFlow<FriendshipDialogViewState> = _friendshipDialogViewState

    val appTheme: StateFlow<AppTheme> = _appTheme

    val mainPageBottomBarViewState: StateFlow<MainPageBottomBarViewState> =
        _mainPageBottomBarViewState

    val personProfilePageViewState: StateFlow<PersonProfilePageViewState> =
        _personProfilePageViewState

    val conversationPageViewState: StateFlow<ConversationPageViewState> =
        _conversationPageViewState

    val friendshipPageViewState: StateFlow<FriendshipPageViewState> = _friendshipPageViewState

    init {
        viewModelScope.launch(context = Dispatchers.Main.immediate) {
            launch(context = Dispatchers.Main.immediate) {
                launch(context = Dispatchers.Main.immediate) {
                    ComposeChat.conversationProvider.conversationList.collect {
                        _conversationPageViewState.emit(
                            value = _conversationPageViewState.value.copy(conversationList = it)
                        )
                    }
                }
                launch(context = Dispatchers.Main.immediate) {
                    ComposeChat.conversationProvider.totalUnreadMessageCount.collect {
                        _mainPageBottomBarViewState.emit(
                            value = _mainPageBottomBarViewState.value.copy(unreadMessageCount = it)
                        )
                    }
                }
            }
            launch(context = Dispatchers.Main.immediate) {
                launch(context = Dispatchers.Main.immediate) {
                    ComposeChat.groupProvider.joinedGroupList.collect {
                        _friendshipPageViewState.emit(
                            value = _friendshipPageViewState.value.copy(joinedGroupList = it)
                        )
                    }
                }
            }
            launch(context = Dispatchers.Main.immediate) {
                ComposeChat.friendshipProvider.friendList.collect {
                    _friendshipPageViewState.emit(
                        value = _friendshipPageViewState.value.copy(friendList = it)
                    )
                }
            }
            launch(context = Dispatchers.Main.immediate) {
                launch(context = Dispatchers.Main.immediate) {
                    ComposeChat.accountProvider.personProfile.collect {
                        _personProfilePageViewState.emit(
                            value = _personProfilePageViewState.value.copy(personProfile = it)
                        )
                        _mainPageDrawerViewState.emit(
                            value = _mainPageDrawerViewState.value.copy(personProfile = it)
                        )
                    }
                }
                launch(context = Dispatchers.Main.immediate) {
                    ComposeChat.accountProvider.serverConnectState.collect {
                        _serverConnectState.emit(value = it)
                        if (it == ServerState.ConnectSuccess) {
                            requestData()
                        }
                    }
                }
            }
            launch(context = Dispatchers.Main.immediate) {
                requestData()
            }
        }
    }

    private fun requestData() {
        ComposeChat.conversationProvider.getConversationList()
        ComposeChat.conversationProvider.getTotalUnreadMessageCount()
        ComposeChat.friendshipProvider.getFriendList()
        ComposeChat.groupProvider.getJoinedGroupList()
        ComposeChat.accountProvider.getPersonProfile()
    }

    fun onTabSelected(tab: MainTab) {
        viewModelScope.launch {
            _mainPageBottomBarViewState.emit(
                value = _mainPageBottomBarViewState.value.copy(tabSelected = tab)
            )
        }
    }

    fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
            val result = when (conversation) {
                is C2CConversation -> {
                    ComposeChat.conversationProvider.deleteC2CConversation(userId = conversation.id)
                }
                is GroupConversation -> {
                    ComposeChat.conversationProvider.deleteGroupConversation(groupId = conversation.id)
                }
            }
            when (result) {
                is ActionResult.Success -> {
                    ComposeChat.conversationProvider.getConversationList()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun pinConversation(conversation: Conversation, pin: Boolean) {
        viewModelScope.launch {
            ComposeChat.conversationProvider.pinConversation(conversation = conversation, pin = pin)
        }
    }

    fun showPageFriendshipPanel() {
        viewModelScope.launch {
            _friendshipDialogViewState.emit(value = _friendshipDialogViewState.value.copy(visible = true))
        }
    }

    private fun onFriendshipPanelDismissRequest() {
        viewModelScope.launch {
            _friendshipDialogViewState.emit(value = _friendshipDialogViewState.value.copy(visible = false))
        }
    }

    private fun addFriend(userId: String) {
        viewModelScope.launch {
            val formatUserId = userId.lowercase()
            when (val result = ComposeChat.friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    showToast("添加成功")
                    ChatActivity.navTo(
                        context = ContextHolder.context,
                        chat = PrivateChat(id = formatUserId)
                    )
                    onFriendshipPanelDismissRequest()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    private fun joinGroup(groupId: String) {
        viewModelScope.launch {
            when (val result = ComposeChat.groupProvider.joinGroup(groupId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    showToast("加入成功")
                    ComposeChat.groupProvider.getJoinedGroupList()
                    onFriendshipPanelDismissRequest()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (val result = ComposeChat.accountProvider.logout()) {
                is ActionResult.Success -> {
                    AccountCache.onUserLogout()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun switchToNextTheme() {
        viewModelScope.launch {
            val nextTheme = appTheme.value.nextTheme()
            AppThemeCache.onAppThemeChanged(appTheme = nextTheme)
            _appTheme.emit(value = nextTheme)
            _mainPageDrawerViewState.emit(
                value = _mainPageDrawerViewState.value.copy(appTheme = nextTheme)
            )
        }
    }

}