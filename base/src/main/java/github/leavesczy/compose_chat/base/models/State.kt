package github.leavesczy.compose_chat.base.models

import androidx.compose.runtime.Stable

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
sealed class ActionResult {

    data object Success : ActionResult()

    data class Failed(val code: Int, val reason: String) : ActionResult() {

        val desc = "$code $reason"

        constructor(reason: String) : this(code = -1, reason = reason)

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
    KickedOffline
}