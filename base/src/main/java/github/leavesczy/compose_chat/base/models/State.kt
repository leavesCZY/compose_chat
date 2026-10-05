package github.leavesczy.compose_chat.base.models

sealed class ActionResult {

    data object Success : ActionResult()

    data class Failed(
        private val code: Int,
        private val reason: String?
    ) : ActionResult() {

        constructor(reason: String?) : this(code = -1, reason = reason)

        val desc = "$code $reason"

    }

}

enum class ServerConnectState {
    Idle,
    Logout,
    Connecting,
    Connected,
    ConnectFailed,
    UserSigExpired,
    KickedOffline;
}
