package github.leavesczy.compose_chat.ui.login.logic

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class LoginPageViewState(
    val userId: TextFieldValue,
    val onUserIdInputChanged: (TextFieldValue) -> Unit,
    val showPanel: Boolean,
    val loading: Boolean
)