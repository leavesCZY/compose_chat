package github.leavesczy.compose_chat.proxy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
internal object AppCoroutineScope : CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate

}