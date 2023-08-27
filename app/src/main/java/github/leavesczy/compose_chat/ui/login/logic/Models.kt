package github.leavesczy.compose_chat.ui.login.logic

import androidx.compose.runtime.Stable

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class LoginPageViewState(
    val lastLoginUserId: String,
    val showPanel: Boolean,
    val loading: Boolean
)