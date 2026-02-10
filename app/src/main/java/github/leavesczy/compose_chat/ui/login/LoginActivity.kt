package github.leavesczy.compose_chat.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.ui.MainActivity
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.login.logic.LoginViewModel
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class LoginActivity : BaseActivity() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPage(
                viewState = loginViewModel.loginPageViewState,
                onClickLoginButton = ::onClickLoginButton
            )
        }
        tryLogin()
    }

    private fun tryLogin() {
        lifecycleScope.launch {
            val result = loginViewModel.tryLogin()
            if (result) {
                startActivity<MainActivity>()
                finish()
            }
        }
    }

    private fun onClickLoginButton() {
        lifecycleScope.launch {
            val result = loginViewModel.onClickLoginButton()
            if (result) {
                startActivity<MainActivity>()
                finish()
            }
        }
    }

}