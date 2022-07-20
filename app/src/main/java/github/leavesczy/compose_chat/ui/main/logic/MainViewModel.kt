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
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
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

    val conversationPageViewState: StateFlow<ConversationPageViewState> =
        _conversationPageViewState

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

    val friendshipPageViewState: StateFlow<FriendshipPageViewState> = _friendshipPageViewState

    private val _personProfilePageViewState =
        MutableStateFlow(value = PersonProfilePageViewState(personProfile = PersonProfile.Empty))

    val personProfilePageViewState: StateFlow<PersonProfilePageViewState> =
        _personProfilePageViewState

    private val _mainPageBottomBarViewState = MutableStateFlow(
        value = MainPageBottomBarViewState(
            tabList = MainTab.values().toList(),
            tabSelected = MainTab.Conversation,
            unreadMessageCount = 0
        )
    )

    val mainPageBottomBarViewState: StateFlow<MainPageBottomBarViewState> =
        _mainPageBottomBarViewState

    private val _appTheme = MutableStateFlow(value = AppThemeCache.currentTheme)

    val appTheme: StateFlow<AppTheme> = _appTheme

    private val _serverConnectState = MutableStateFlow(value = ServerState.ConnectSuccess)

    val serverConnectState: SharedFlow<ServerState> = _serverConnectState

    private val _mainPageDrawerViewState = MutableStateFlow(
        value = MainPageDrawerViewState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            appTheme = appTheme.value,
            personProfile = ComposeChat.accountProvider.personProfile.value
        )
    )

    val mainPageDrawerViewState: StateFlow<MainPageDrawerViewState> = _mainPageDrawerViewState

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

    suspend fun addFriend(userId: String): ActionResult {
        val formatUserId = userId.lowercase()
        return ComposeChat.friendshipProvider.addFriend(friendId = formatUserId)
    }

    suspend fun joinGroup(groupId: String): ActionResult {
        return ComposeChat.groupProvider.joinGroup(groupId)
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
            AppThemeCache.onAppThemeChanged(nextTheme)
            _appTheme.emit(value = nextTheme)
            _mainPageDrawerViewState.emit(
                value = _mainPageDrawerViewState.value.copy(appTheme = nextTheme)
            )
        }
    }

}