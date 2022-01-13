package github.leavesczy.compose_chat

import android.app.Application
import github.leavesczy.compose_chat.cache.AppThemeCache
import github.leavesczy.compose_chat.logic.ComposeChat
import github.leavesczy.compose_chat.ui.weigets.CoilImageLoader
import github.leavesczy.compose_chat.utils.ContextHolder

/**
 * @Author: leavesCZY
 * @Date: 2021/6/18 23:52
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextHolder.init(context = this)
        CoilImageLoader.init(context = this)
        ComposeChat.accountProvider.init(context = this)
        AppThemeCache.init()
    }

}