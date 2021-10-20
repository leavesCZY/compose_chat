package github.leavesc.compose_chat.proxy.coroutineScope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * @Author: leavesC
 * @Date: 2021/10/20 14:09
 * @Desc:
 * @Github：https://github.com/leavesC
 */
internal class ChatCoroutineScope : CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    fun cancel() {
        coroutineContext.cancel()
    }

}