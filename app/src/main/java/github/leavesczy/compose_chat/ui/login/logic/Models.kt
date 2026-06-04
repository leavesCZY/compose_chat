package github.leavesczy.compose_chat.ui.login.logic

import android.app.Activity
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
@Stable
data class LoginPageViewState(
    val userId: TextFieldValue,
    val isPanelVisible: Boolean,
    val onUserIdInputChanged: (userId: TextFieldValue) -> Unit,
    val onClickLogin: (activity: Activity) -> Unit
)