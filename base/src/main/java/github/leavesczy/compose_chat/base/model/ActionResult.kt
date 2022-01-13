package github.leavesczy.compose_chat.base.model

/**
 * @Author: leavesCZY
 * @Date: 2021/6/20 22:23
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
sealed class ActionResult {

    object Success : ActionResult()

    data class Failed(val code: Int, val reason: String) : ActionResult() {

        constructor(reason: String) : this(code = -1, reason = reason)

    }

}

sealed class LoadMessageResult {

    data class Success(val messageList: List<Message>, val loadFinish: Boolean) :
        LoadMessageResult()

    data class Failed(val reason: String) : LoadMessageResult()

}

enum class ServerState {
    Nothing,
    Logout,
    Connecting,
    ConnectSuccess,
    ConnectFailed,
    UserSigExpired,
    KickedOffline
}