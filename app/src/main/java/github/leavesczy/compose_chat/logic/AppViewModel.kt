package github.leavesczy.compose_chat.logic

import androidx.lifecycle.ViewModel
import github.leavesczy.compose_chat.cache.AppThemeCache
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @Author: leavesCZY
 * @Date: 2021/11/6 22:00
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class AppViewModel : ViewModel() {

    val appTheme = MutableStateFlow(AppThemeCache.currentTheme)

    val serverConnectState = ComposeChat.accountProvider.serverConnectState

    fun switchToNextTheme() {
        val nextTheme = appTheme.value.nextTheme()
        AppThemeCache.onAppThemeChanged(nextTheme)
        appTheme.value = nextTheme
    }

}