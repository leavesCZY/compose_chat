package github.leavesczy.compose_chat.base.utils

object StringResources {

    fun getString(resId: Int): String {
        return ContextProvider.get().getString(resId)
    }

    fun getString(resId: Int, vararg formatArgs: Any): String {
        return ContextProvider.get().getString(resId, *formatArgs)
    }

}