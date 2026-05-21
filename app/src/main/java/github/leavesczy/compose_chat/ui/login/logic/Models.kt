package github.leavesczy.compose_chat.ui.login.logic

import android.app.Activity
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
@Stable
data class LoginPageViewState(
    val userId: TextFieldValue,
    val panelVisible: Boolean,
    val onUserIdInputChanged: (userId: TextFieldValue) -> Unit,
    val onClickLogin: (activity: Activity) -> Unit
)