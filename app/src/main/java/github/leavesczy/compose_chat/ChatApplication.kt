package github.leavesczy.compose_chat

import android.app.Application
import github.leavesczy.compose_chat.base.utils.ContextProvider
import github.leavesczy.compose_chat.di.ComposeChat
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.provider.LoginPreferences
import github.leavesczy.compose_chat.utils.CompressImageUtils
import github.leavesczy.compose_chat.utils.ImageUtils

class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.init(application = this)
        AppThemeProvider.init(application = this)
        LoginPreferences.init(application = this)
        ComposeChat.accountProvider.init(application = this)
        ImageUtils.init()
        CompressImageUtils.cleanupOldTempFiles(context = this)
    }

}
