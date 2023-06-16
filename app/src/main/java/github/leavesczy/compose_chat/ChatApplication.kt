package github.leavesczy.compose_chat

import android.app.Application
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.provider.ContextProvider
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import github.leavesczy.compose_chat.ui.widgets.CoilImageLoader

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.init(application = this)
        ToastProvider.init(application = this)
        AppThemeProvider.init(application = this)
        AccountProvider.init(application = this)
        CoilImageLoader.init(application = this)
        ComposeChat.accountProvider.init(application = this)
    }

}