package github.leavesczy.compose_chat.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import github.leavesczy.compose_chat.base.BaseActivity
import github.leavesczy.compose_chat.ui.login.logic.LoginViewModel
import github.leavesczy.compose_chat.widgets.LoadingDialog

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
 * @Desc:
 */
class LoginActivity : BaseActivity() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPage(pageViewState = loginViewModel.pageViewState)
            LoadingDialog(viewState = loginViewModel.loadingDialogViewState)
        }
        loginViewModel.tryAutoLogin(activity = this)
    }

}