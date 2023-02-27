package github.leavesczy.compose_chat.utils

import android.app.Application

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object ContextHolder {

    lateinit var context: Application
        private set

    fun init(application: Application) {
        this.context = application
    }

}