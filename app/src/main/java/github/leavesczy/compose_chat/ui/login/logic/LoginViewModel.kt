package github.leavesczy.compose_chat.ui.login.logic

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.BaseViewModel
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.ui.main.MainActivity
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.provider.LoginPreferences
import kotlinx.coroutines.launch

class LoginViewModel : BaseViewModel() {

    var pageViewState by mutableStateOf(
        value = buildLoginPageViewState()
    )
        private set

    private fun buildLoginPageViewState(): LoginPageViewState {
        val lastLoginUserId = LoginPreferences.lastLoginUserId
        val userId = TextFieldValue(
            text = lastLoginUserId,
            selection = TextRange(index = lastLoginUserId.length)
        )
        val autoLogin = lastLoginUserId.isNotBlank() && LoginPreferences.isAutoLoginEnabled
        return LoginPageViewState(
            isPanelVisible = !autoLogin,
            userId = userId,
            onUserIdInputChanged = ::onUserIdInputChanged,
            onClickLogin = ::onClickLogin
        )
    }

    private fun onUserIdInputChanged(input: TextFieldValue) {
        val trimText = input.text.trim()
        val isAvailable = trimText.length <= 12 && trimText.all { char ->
            char.isLowerCase() || char.isUpperCase()
        }
        if (isAvailable) {
            pageViewState = pageViewState.copy(userId = input.copy(text = trimText))
        }
    }

    fun tryAutoLogin(activity: Activity) {
        viewModelScope.launch {
            val viewState = pageViewState
            val isPanelVisible = viewState.isPanelVisible
            val userId = viewState.userId.text
            if (!isPanelVisible && userId.isNotBlank()) {
                showLoadingDialog()
                val isSuccess = login(userId = userId)
                if (isSuccess) {
                    navToMainActivityAndFinish(activity = activity)
                } else {
                    pageViewState = viewState.copy(isPanelVisible = true)
                }
                dismissLoadingDialog()
            }
        }
    }

    private fun onClickLogin(activity: Activity) {
        viewModelScope.launch {
            val userId = pageViewState.userId.text
            if (userId.isBlank()) {
                return@launch
            }
            showLoadingDialog()
            val isSuccess = login(userId = userId)
            if (isSuccess) {
                navToMainActivityAndFinish(activity = activity)
            }
            dismissLoadingDialog()
        }
    }

    private suspend fun login(userId: String): Boolean {
        val formatUserId = userId.trim().lowercase()
        return when (val result = ComposeChat.accountProvider.login(userId = formatUserId)) {
            is ActionResult.Success -> {
                LoginPreferences.onUserLogin(userId = formatUserId)
                true
            }

            is ActionResult.Failed -> {
                showToast(msg = result.desc)
                false
            }
        }
    }

    private fun navToMainActivityAndFinish(activity: Activity) {
        activity.startActivity<MainActivity>()
        activity.finish()
    }

}