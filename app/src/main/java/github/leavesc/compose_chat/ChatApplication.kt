package github.leavesc.compose_chat

import android.app.Application
import github.leavesc.compose_chat.cache.AppThemeCache
import github.leavesc.compose_chat.logic.Chat
import github.leavesc.compose_chat.ui.common.CoilImageLoader
import github.leavesc.compose_chat.utils.ContextHolder

/**
 * @Author: leavesC
 * @Date: 2021/6/18 23:52
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextHolder.context = this
        AppThemeCache.initTheme()
        CoilImageLoader.initImageLoader(context = this)
        Chat.accountProvider.init(context = this)
    }

}