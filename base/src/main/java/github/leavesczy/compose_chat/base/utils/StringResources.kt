package github.leavesczy.compose_chat.base.utils

import androidx.annotation.StringRes

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
object StringResources {

    fun getString(@StringRes resId: Int): String {
        return ContextProvider.get().getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return ContextProvider.get().getString(resId, *formatArgs)
    }

}
