package github.leavesczy.compose_chat.ui.login.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.cache.AccountCache
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.model.LoginPageAction
import github.leavesczy.compose_chat.model.LoginPageViewState
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/7/9 15:10
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class LoginViewModel : ViewModel() {

    private val _loginPageViewState = MutableStateFlow(
        LoginPageViewState(
            showPanel = false,
            showLoadingDialog = false,
            lastLoginUserId = ""
        )
    )

    val loginPageViewState: StateFlow<LoginPageViewState> = _loginPageViewState

    private val _loginPageAction = MutableSharedFlow<LoginPageAction>()

    val loginPageAction: SharedFlow<LoginPageAction> = _loginPageAction

    fun autoLogin() {
        val lastLoginUserId = AccountCache.lastLoginUserId
        if (lastLoginUserId.isBlank() || !AccountCache.canAutoLogin) {
            dispatchViewState(
                LoginPageViewState(
                    showPanel = true,
                    showLoadingDialog = false,
                    lastLoginUserId = lastLoginUserId
                )
            )
        } else {
            dispatchViewState(
                LoginPageViewState(
                    showPanel = false,
                    showLoadingDialog = true,
                    lastLoginUserId = lastLoginUserId
                )
            )
            login(userId = lastLoginUserId)
        }
    }

    fun goToLogin(userId: String) {
        dispatchViewState(
            LoginPageViewState(
                showPanel = true,
                showLoadingDialog = true,
                lastLoginUserId = userId
            )
        )
        login(userId = userId)
    }

    private fun login(userId: String) {
        val formatUserId = userId.lowercase()
        viewModelScope.launch {
            when (val loginResult = ComposeChat.accountProvider.login(userId = formatUserId)) {
                is ActionResult.Failed -> {
                    showToast(loginResult.reason)
                    dispatchViewState(
                        LoginPageViewState(
                            showPanel = true,
                            showLoadingDialog = false,
                            lastLoginUserId = formatUserId
                        )
                    )
                }
                is ActionResult.Success -> {
                    delay(timeMillis = 400)
                    AccountCache.onUserLogin(userId = formatUserId)
                    _loginPageAction.emit(value = LoginPageAction.LoginSuccess)
                }
            }
        }
    }

    private fun dispatchViewState(loginPageViewState: LoginPageViewState) {
        viewModelScope.launch {
            _loginPageViewState.emit(loginPageViewState)
        }
    }

}