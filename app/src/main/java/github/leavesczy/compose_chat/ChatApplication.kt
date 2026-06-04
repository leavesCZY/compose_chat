package github.leavesczy.compose_chat

import android.app.Application
import github.leavesczy.compose_chat.base.utils.ContextProvider
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.provider.LoginPreferences
import github.leavesczy.compose_chat.utils.ImageUtils

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.init(application = this)
        AppThemeProvider.init(application = this)
        LoginPreferences.init(application = this)
        ComposeChat.accountProvider.init(application = this)
        ImageUtils.init()
    }

}