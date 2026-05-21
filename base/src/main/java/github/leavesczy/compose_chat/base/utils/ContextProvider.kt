package github.leavesczy.compose_chat.base.utils

import android.app.Application
import android.content.Context

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
object ContextProvider {

    private lateinit var context: Application

    fun init(application: Application) {
        context = application
    }

    fun get(): Context {
        return context
    }

}