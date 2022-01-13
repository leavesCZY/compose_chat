package github.leavesczy.compose_chat.proxy.coroutineScope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * @Author: leavesCZY
 * @Date: 2021/10/20 14:09
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal class ChatCoroutineScope : CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    fun cancel() {
        coroutineContext.cancel()
    }

}