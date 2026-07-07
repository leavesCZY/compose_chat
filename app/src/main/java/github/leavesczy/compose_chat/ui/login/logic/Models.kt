package github.leavesczy.compose_chat.ui.login.logic

import android.app.Activity
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue

@Stable
data class LoginPageViewState(
    val userId: TextFieldValue,
    val isPanelVisible: Boolean,
    val onUserIdInputChanged: (userId: TextFieldValue) -> Unit,
    val onClickLogin: (activity: Activity) -> Unit
)