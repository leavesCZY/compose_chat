package github.leavesc.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.cache.AccountHolder
import github.leavesc.compose_chat.model.LoginScreenState
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

/**
 * @Author: leavesC
 * @Date: 2021/7/9 15:10
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class LoginViewModel : ViewModel() {

    val loginScreenState = MutableStateFlow(
        LoginScreenState(
            showLogo = false,
            showInput = false,
            showLoading = false,
            loginSuccess = false,
            lastLoginUserId = ""
        )
    )

    fun autoLogin() {
        val lastLoginUserId = AccountHolder.lastLoginUserId
        if (lastLoginUserId.isBlank() || !AccountHolder.canAutoLogin) {
            dispatchViewState(
                LoginScreenState(
                    showLogo = true,
                    showInput = true,
                    showLoading = false,
                    loginSuccess = false,
                    lastLoginUserId = lastLoginUserId
                )
            )
        } else {
            dispatchViewState(
                LoginScreenState(
                    showLogo = false,
                    showInput = false,
                    showLoading = true,
                    loginSuccess = false,
                    lastLoginUserId = lastLoginUserId
                )
            )
            login(userId = lastLoginUserId)
        }
    }

    fun goToLogin(userId: String) {
        dispatchViewState(
            LoginScreenState(
                showLogo = true,
                showInput = true,
                showLoading = true,
                loginSuccess = false,
                lastLoginUserId = userId
            )
        )
        login(userId = userId)
    }

    private fun login(userId: String) {
        val formatUserId = userId.lowercase()
        viewModelScope.launch(Dispatchers.Main) {
            val loginResult: ActionResult
            val time = measureTimeMillis {
                loginResult = Chat.accountProvider.login(formatUserId)
            }
            when (loginResult) {
                is ActionResult.Failed -> {
                    showToast(loginResult.reason)
                    dispatchViewState(
                        LoginScreenState(
                            showLogo = true,
                            showInput = true,
                            showLoading = false,
                            loginSuccess = false,
                            lastLoginUserId = formatUserId
                        )
                    )
                }
                is ActionResult.Success -> {
//                    val minTime = 2000
//                    if (time < minTime) {
//                        delay(minTime - time)
//                    }
                    AccountHolder.onUserLogin(userId = formatUserId)
                    dispatchViewState(
                        LoginScreenState(
                            showLogo = false,
                            showInput = false,
                            showLoading = false,
                            loginSuccess = true,
                            lastLoginUserId = formatUserId
                        )
                    )
                }
            }
        }
    }

    private fun dispatchViewState(loginScreenState: LoginScreenState) {
        this.loginScreenState.value = loginScreenState
    }

}