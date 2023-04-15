package github.leavesczy.compose_chat.base.model

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
sealed class ActionResult {

    object Success : ActionResult()

    data class Failed(
        val code: Int, val reason: String
    ) : ActionResult() {

        constructor(reason: String) : this(
            code = -1, reason = reason
        )

    }

}

enum class ServerState {
    Logout,
    Connecting,
    ConnectSuccess,
    ConnectFailed,
    UserSigExpired,
    KickedOffline
}