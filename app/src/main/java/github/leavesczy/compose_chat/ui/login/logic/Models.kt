package github.leavesczy.compose_chat.ui.login.logic

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class LoginPageViewState(
    val lastLoginUserId: MutableState<String>,
    val showPanel: MutableState<Boolean>,
    val loading: MutableState<Boolean>
)