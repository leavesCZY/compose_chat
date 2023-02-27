package github.leavesczy.compose_chat.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.login.logic.LoginPageAction
import github.leavesczy.compose_chat.ui.login.logic.LoginViewModel
import github.leavesczy.compose_chat.ui.main.MainActivity
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
            LoginPage(loginViewModel = loginViewModel)
        }
        initEvent()
        loginViewModel.tryLogin()
    }

    private fun initEvent() {
        lifecycleScope.launch {
            loginViewModel.loginPageAction.collect {
                when (it) {
                    LoginPageAction.LoginSuccess -> {
                        startActivity<MainActivity>()
                        finish()
                    }
                }
            }
        }
    }

}