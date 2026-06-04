package github.leavesczy.compose_chat.base.utils

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
object StringResources {

    fun getString(resId: Int): String {
        return ContextProvider.get().getString(resId)
    }

    fun getString(resId: Int, vararg formatArgs: Any): String {
        return ContextProvider.get().getString(resId, *formatArgs)
    }

}