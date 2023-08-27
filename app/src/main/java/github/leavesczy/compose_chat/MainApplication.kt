package github.leavesczy.compose_chat

import android.app.Application
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.provider.ContextProvider
import github.leavesczy.compose_chat.provider.ImageLoaderProvider
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.logic.ComposeChat

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.init(application = this)
        ToastProvider.init(application = this)
        AppThemeProvider.init(application = this)
        AccountProvider.init(application = this)
        ImageLoaderProvider.init(application = this)
        ComposeChat.accountProvider.init(application = this)
    }

}