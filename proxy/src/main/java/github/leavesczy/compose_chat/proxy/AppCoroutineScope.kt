package github.leavesczy.compose_chat.proxy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal object AppCoroutineScope : CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate

}