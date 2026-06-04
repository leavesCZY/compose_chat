package github.leavesczy.compose_chat.base.models

import androidx.compose.runtime.Stable

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
@Stable
sealed class ActionResult {

    @Stable
    data object Success : ActionResult()

    @Stable
    data class Failed(
        private val code: Int,
        private val reason: String?
    ) : ActionResult() {

        constructor(reason: String?) : this(code = -1, reason = reason)

        val desc = "$code $reason"

    }

}

@Stable
enum class ServerConnectState {
    Idle,
    Logout,
    Connecting,
    Connected,
    ConnectFailed,
    UserSigExpired,
    KickedOffline;
}