package github.leavesczy.compose_chat.ui.login.logic

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
enum class LoginPageAction {
    LoginSuccess
}

data class LoginPageViewState(
    val lastLoginUserId: String,
    val showPanel: Boolean,
    val loading: Boolean
)