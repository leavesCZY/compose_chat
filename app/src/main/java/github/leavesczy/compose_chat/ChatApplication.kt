package github.leavesczy.compose_chat

import android.app.Application
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.widgets.CoilImageLoader
import github.leavesczy.compose_chat.utils.ContextHolder

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextHolder.init(application = this)
        CoilImageLoader.init(application = this)
        AppThemeProvider.init(application = this)
        AccountProvider.init(application = this)
        ComposeChat.accountProvider.init(application = this)
    }

}