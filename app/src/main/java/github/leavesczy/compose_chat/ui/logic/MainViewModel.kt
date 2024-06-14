package github.leavesczy.compose_chat.ui.logic

import android.content.Intent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.ServerState
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
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainViewModel : BaseViewModel() {

    private val conversationProvider: IConversationProvider = ConversationProvider()

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    val topBarViewState by mutableStateOf(
        value = MainPageTopBarViewState(
            openDrawer = ::openDrawer
        )
    )

    val bottomBarViewState by mutableStateOf(
        value = MainPageBottomBarViewState(
            selectedTab = mutableStateOf(value = MainPageTab.Conversation),
            unreadMessageCount = mutableLongStateOf(value = 0),
            onClickTab = ::onClickTab
        )
    )

    val drawerViewState by mutableStateOf(
        value = MainPageDrawerViewState(
            drawerState = DrawerState(initialValue = DrawerValue.Closed),
            personProfile = mutableStateOf(value = ComposeChat.accountProvider.personProfile.value),
            previewImage = ::previewImage,
            switchTheme = ::switchTheme,
            logout = ::logout,
            updateProfile = ::updateProfile
        )
    )

    private val _serverConnectState = MutableStateFlow(value = ServerState.Connected)

    val serverConnectState: SharedFlow<ServerState> = _serverConnectState

    init {
        viewModelScope.launch {
            launch {
                conversationProvider.totalUnreadMessageCount.collect {
                    bottomBarViewState.unreadMessageCount.value = it
                }
            }
            launch {
                ComposeChat.accountProvider.personProfile.collect {
                    drawerViewState.personProfile.value = it
                }
            }
            launch {
                ComposeChat.accountProvider.serverConnectState.collect {
                    _serverConnectState.emit(value = it)
                    if (it == ServerState.Connected) {
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
        conversationProvider.refreshTotalUnreadMessageCount()
        ComposeChat.accountProvider.refreshPersonProfile()
    }

    private fun onClickTab(mainPageTab: MainPageTab) {
        bottomBarViewState.selectedTab.value = mainPageTab
    }

    private fun logout() {
        viewModelScope.launch {
            loadingDialog(visible = true)
            when (val result = ComposeChat.accountProvider.logout()) {
                is ActionResult.Success -> {
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

    private fun updateProfile() {
        val intent = Intent(context, ProfileUpdateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun previewImage(imageUrl: String) {
        if (imageUrl.isNotBlank()) {
            PreviewImageActivity.navTo(context = context, imageUri = imageUrl)
        }
    }

    private fun switchTheme() {
        val nextTheme = AppThemeProvider.appTheme.nextTheme()
        AppThemeProvider.onAppThemeChanged(appTheme = nextTheme)
    }

    private fun AppTheme.nextTheme(): AppTheme {
        val values = AppTheme.entries
        return values.getOrElse(ordinal + 1) {
            values[0]
        }
    }

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }

}