package github.leavesczy.compose_chat.ui.login

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.MainActivity
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.login.logic.LoginPageViewState
import github.leavesczy.compose_chat.ui.login.logic.LoginViewModel
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog
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
                onClickLoginButton = {
                    onClickLoginButton(userId = it)
                }
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

    private fun onClickLoginButton(userId: String) {
        lifecycleScope.launch {
            val result = loginViewModel.onClickLoginButton(userId = userId)
            if (result) {
                startActivity<MainActivity>()
                finish()
            }
        }
    }

}

@Composable
private fun LoginPage(viewState: LoginPageViewState, onClickLoginButton: (String) -> Unit) {
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
    BackHandler(enabled = viewState.loading) {

    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewState.showPanel) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 0.19f)
                            .wrapContentSize(align = Alignment.BottomCenter),
                        fontSize = 60.sp,
                        fontFamily = FontFamily.Cursive,
                        textAlign = TextAlign.Center
                    )
                    var userId by remember {
                        val lastLoginUserId = viewState.lastLoginUserId
                        mutableStateOf(
                            value = TextFieldValue(
                                text = lastLoginUserId,
                                selection = TextRange(index = lastLoginUserId.length)
                            )
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 60.dp, start = 40.dp, end = 40.dp
                            ),
                        maxLines = 1,
                        singleLine = true,
                        value = userId,
                        label = {
                            Text(text = "UserId", fontSize = 14.sp)
                        },
                        onValueChange = { textField ->
                            val trimText = textField.text.trimStart().trimEnd()
                            if (trimText.length <= 12 && trimText.all {
                                    it.isLowerCase() || it.isUpperCase()
                                }) {
                                userId = textField
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            onClickLoginButton(userId.text)
                        })
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 30.dp, top = 40.dp),
                        content = {
                            Text(
                                modifier = Modifier.padding(vertical = 2.dp),
                                text = "Login",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        },
                        onClick = {
                            val text = userId.text.trim()
                            if (text.isBlank()) {
                                ToastProvider.showToast(msg = "请输入 UserID")
                            } else {
                                localSoftwareKeyboardController?.hide()
                                onClickLoginButton(text)
                            }
                        }
                    )
                }
            }
            LoadingDialog(visible = viewState.loading)
        }
    }
}