package github.leavesczy.compose_chat.ui.login.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import kotlinx.coroutines.delay

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class LoginViewModel : BaseViewModel() {

    var loginPageViewState by mutableStateOf(
        value = LoginPageViewState(
            lastLoginUserId = "",
            showPanel = false,
            loading = false
        )
    )
        private set

    suspend fun tryLogin(): Boolean {
        val lastLoginUserId = AccountProvider.lastLoginUserId
        return if (lastLoginUserId.isBlank() || !AccountProvider.canAutoLogin) {
            loginPageViewState = loginPageViewState.copy(
                lastLoginUserId = lastLoginUserId,
                showPanel = true,
                loading = false
            )
            false
        } else {
            loginPageViewState = loginPageViewState.copy(
                lastLoginUserId = lastLoginUserId,
                showPanel = false,
                loading = true
            )
            login(userId = lastLoginUserId)
        }
    }

    suspend fun onClickLoginButton(userId: String): Boolean {
        loginPageViewState = loginPageViewState.copy(
            lastLoginUserId = userId,
            loading = true
        )
        return login(userId = userId)
    }

    private suspend fun login(userId: String): Boolean {
        val formatUserId = userId.lowercase()
        return when (val loginResult = ComposeChat.accountProvider.login(userId = formatUserId)) {
            is ActionResult.Success -> {
                AccountProvider.onUserLogin(userId = formatUserId)
                delay(timeMillis = 250)
                true
            }

            is ActionResult.Failed -> {
                showToast(msg = loginResult.reason)
                loginPageViewState = loginPageViewState.copy(
                    lastLoginUserId = formatUserId,
                    showPanel = true,
                    loading = false
                )
                false
            }
        }
    }

}