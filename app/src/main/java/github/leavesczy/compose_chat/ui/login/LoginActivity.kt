package github.leavesczy.compose_chat.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.login.logic.LoginViewModel
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
 */
class LoginActivity : BaseActivity() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPage(viewState = loginViewModel.loginPageViewState)
            LoadingDialog(viewState = loginViewModel.loadingDialogViewState)
        }
        loginViewModel.tryAutoLogin(activity = this)
    }

}