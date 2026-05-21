package github.leavesczy.compose_chat.ui.logic

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.base.provider.IConversationProvider
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.proxy.ConversationProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.profile.ProfileUpdateActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
class MainViewModel : BaseViewModel() {

    private val conversationProvider: IConversationProvider = ConversationProvider()

    private val _serverConnectState = MutableStateFlow(value = ServerConnectState.Connected)

    val serverConnectState: SharedFlow<ServerConnectState> = _serverConnectState

    val topBarViewState = MainPageTopBarViewState(
        openDrawer = ::openDrawer
    )

    var bottomBarViewState by mutableStateOf(
        value = MainPageBottomBarViewState(
            selectedTab = MainPageTab.Conversation,
            unreadMessageCount = 0L,
            onClickTab = ::onClickTab
        )
    )
        private set

    var drawerViewState by mutableStateOf(
        value = MainPageDrawerViewState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            appTheme = AppThemeProvider.appTheme,
            personProfile = ComposeChat.accountProvider.personProfileFlow.value,
            previewImage = ::previewImage,
            switchTheme = ::switchTheme,
            logout = ::logout,
            updateProfile = ::updateProfile
        )
    )
        private set

    init {
        viewModelScope.launch {
            launch {
                requestData()
            }
            launch {
                conversationProvider.totalUnreadMessageCountFlow.collect { unreadMessageCount ->
                    onUnreadMessageCountChanged(unreadMessageCount = unreadMessageCount)
                }
            }
            launch {
                ComposeChat.accountProvider.personProfileFlow.collect { personProfile ->
                    onPersonProfileChanged(personProfile = personProfile)
                }
            }
            launch {
                ComposeChat.accountProvider.serverConnectStateFlow.collect { state ->
                    _serverConnectState.emit(value = state)
                    if (state == ServerConnectState.Connected) {
                        requestData()
                    }
                }
            }
        }
    }

    private suspend fun requestData() {
        conversationProvider.refreshTotalUnreadMessageCount()
        ComposeChat.accountProvider.refreshPersonProfile()
    }

    private fun onClickTab(mainPageTab: MainPageTab) {
        val viewState = bottomBarViewState
        if (viewState.selectedTab != mainPageTab) {
            bottomBarViewState = viewState.copy(selectedTab = mainPageTab)
        }
    }

    private fun onUnreadMessageCountChanged(unreadMessageCount: Long) {
        val viewState = bottomBarViewState
        if (viewState.unreadMessageCount != unreadMessageCount) {
            bottomBarViewState = viewState.copy(unreadMessageCount = unreadMessageCount)
        }
    }

    private fun onPersonProfileChanged(personProfile: PersonProfile) {
        val viewState = drawerViewState
        if (drawerViewState.personProfile != personProfile) {
            drawerViewState = viewState.copy(personProfile = personProfile)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            showLoadingDialog()
            when (val result = ComposeChat.accountProvider.logout()) {
                is ActionResult.Success -> {
                    AccountProvider.onUserLogout()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.desc)
                }
            }
            dismissLoadingDialog()
        }
    }

    private suspend fun openDrawer() {
        drawerViewState.drawerState.open()
    }

    private fun updateProfile() {
        context.startActivity<ProfileUpdateActivity>()
    }

    private fun previewImage(imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            PreviewImageActivity.navTo(context = context, imageUri = imageUrl)
        }
    }

    private fun switchTheme() {
        val nextTheme = AppThemeProvider.appTheme.nextTheme()
        drawerViewState = drawerViewState.copy(appTheme = nextTheme)
        AppThemeProvider.onAppThemeChanged(appTheme = nextTheme)
    }

    private fun AppTheme.nextTheme(): AppTheme {
        val values = AppTheme.entries
        return values.getOrElse(ordinal + 1) {
            values[0]
        }
    }

}