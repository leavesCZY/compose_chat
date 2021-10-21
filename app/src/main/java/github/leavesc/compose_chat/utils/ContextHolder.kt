package github.leavesc.compose_chat.utils

import android.app.Application

/**
 * @Author: leavesC
 * @Date: 2021/6/30 22:21
 * @Desc:
 * @Github：https://github.com/leavesC
 */
object ContextHolder {

    lateinit var context: Application
        private set

    fun init(context: Application) {
        this.context = context
    }

}