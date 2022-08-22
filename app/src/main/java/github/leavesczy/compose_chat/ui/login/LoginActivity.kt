package github.leavesczy.compose_chat.ui.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.login.logic.LoginViewModel
import github.leavesczy.compose_chat.ui.main.MainActivity
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import kotlinx.coroutines.launch

/**
 * @Author: CZY
 * @Date: 2022/7/17 13:56
 * @Desc:
 */
class LoginActivity : BaseActivity() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeChatTheme {
                val loginPageState by loginViewModel.loginPageViewState.collectAsState()
                LoginPage(viewState = loginPageState, login = {
                    loginViewModel.goToLogin(userId = it)
                })
            }
        }
        lifecycleScope.launch {
            loginViewModel.loginSuccess.collect {
                if (it) {
                    startActivity<MainActivity>()
                    finish()
                }
            }
        }
        loginViewModel.autoLogin()
    }

}