package github.leavesczy.compose_chat.ui.login.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class LoginViewModel : ViewModel() {

    var loginPageViewState by mutableStateOf(
        value = LoginPageViewState(
            showPanel = false,
            loading = false,
            lastLoginUserId = ""
        )
    )
        private set

    private val _loginPageAction = MutableSharedFlow<LoginPageAction>()

    val loginPageAction: SharedFlow<LoginPageAction> = _loginPageAction

    fun tryLogin() {
        val lastLoginUserId = AccountProvider.lastLoginUserId
        if (lastLoginUserId.isBlank() || !AccountProvider.canAutoLogin) {
            loginPageViewState = LoginPageViewState(
                showPanel = true,
                loading = false,
                lastLoginUserId = lastLoginUserId
            )
        } else {
            loginPageViewState = LoginPageViewState(
                showPanel = false,
                loading = true,
                lastLoginUserId = lastLoginUserId
            )
            login(userId = lastLoginUserId)
        }
    }

    fun goToLogin(userId: String) {
        loginPageViewState = LoginPageViewState(
            showPanel = true,
            loading = true,
            lastLoginUserId = userId
        )
        login(userId = userId)
    }

    private fun login(userId: String) {
        val formatUserId = userId.lowercase()
        viewModelScope.launch {
            when (val loginResult = ComposeChat.accountProvider.login(userId = formatUserId)) {
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    AccountProvider.onUserLogin(userId = formatUserId)
                    _loginPageAction.emit(value = LoginPageAction.LoginSuccess)
                }
                is ActionResult.Failed -> {
                    showToast(msg = loginResult.reason)
                    loginPageViewState = LoginPageViewState(
                        showPanel = true,
                        loading = false,
                        lastLoginUserId = formatUserId
                    )
                }
            }
        }
    }

}