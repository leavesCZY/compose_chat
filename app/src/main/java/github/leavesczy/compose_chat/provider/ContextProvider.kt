package github.leavesczy.compose_chat.provider

import android.app.Application

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object ContextProvider {

    lateinit var context: Application
        private set

    fun init(application: Application) {
        context = application
    }

}