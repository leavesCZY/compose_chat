package github.leavesczy.compose_chat.ui.main.logic

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.theme.AppThemeMode
import github.leavesczy.compose_chat.ui.main.conversation.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.profile.ProfileUpdateActivity
import github.leavesczy.compose_chat.ui.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.provider.LoginPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ConversationViewModel() {

    private val _serverConnectState = MutableStateFlow(value = ServerConnectState.Connected)

    val serverConnectState: SharedFlow<ServerConnectState> = _serverConnectState

    var topBarViewState by mutableStateOf(
        value = MainPageTopBarViewState(
            onClickOpenDrawer = ::openDrawer,
            onClickShowFriendshipDialog = ::showFriendshipDialog
        )
    )
        private set

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
            appTheme = AppThemeProvider.appThemeMode,
            personProfile = accountProvider.personProfileFlow.value,
            onClickPreviewImage = ::previewImage,
            onClickSwitchTheme = ::switchTheme,
            onClickLogout = ::logout,
            onClickUpdateProfile = ::updateProfile
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
                accountProvider.personProfileFlow.collect { personProfile ->
                    onPersonProfileChanged(personProfile = personProfile)
                }
            }
            launch {
                accountProvider.serverConnectStateFlow.collect { state ->
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
        accountProvider.refreshPersonProfile()
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
            when (val result = accountProvider.logout()) {
                is ActionResult.Success -> {
                    LoginPreferences.onUserLogout()
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
        val nextTheme = AppThemeProvider.appThemeMode.nextTheme()
        drawerViewState = drawerViewState.copy(appTheme = nextTheme)
        AppThemeProvider.onAppThemeModeChanged(appThemeMode = nextTheme)
    }

    private fun AppThemeMode.nextTheme(): AppThemeMode {
        val values = AppThemeMode.entries
        return values.getOrElse(index = ordinal + 1, defaultValue = { values[0] })
    }

}