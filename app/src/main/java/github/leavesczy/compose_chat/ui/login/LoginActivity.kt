package github.leavesczy.compose_chat.ui.login

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
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

@Composable
private fun LoginPage(
    viewState: LoginPageViewState,
    onClickLoginButton: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets(
            left = 0.dp,
            right = 0.dp,
            top = 0.dp,
            bottom = 0.dp
        )
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
            val context = LocalContext.current
            BackHandler(enabled = viewState.loading) {

            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewState.showPanel) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 0.19f)
                            .wrapContentSize(align = Alignment.BottomCenter),
                        text = stringResource(id = R.string.app_name),
                        style = TextStyle(
                            fontSize = 64.sp,
                            fontFamily = FontFamily.Cursive,
                            textAlign = TextAlign.Center,
                            shadow = Shadow(
                                offset = Offset(5.4f, 12f),
                                blurRadius = 3f
                            )
                        )
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp, start = 40.dp, end = 40.dp),
                        value = viewState.userId,
                        onValueChange = viewState.onUserIdInputChanged,
                        maxLines = 1,
                        singleLine = true,
                        label = {
                            Text(
                                modifier = Modifier,
                                text = "UserId",
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            onClickLoginButton()
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
                            val input = viewState.userId.text
                            if (input.isBlank()) {
                                ToastProvider.showToast(context = context, msg = "请输入 UserID")
                            } else {
                                localSoftwareKeyboardController?.hide()
                                onClickLoginButton()
                            }
                        }
                    )
                }
            }
            LoadingDialog(visible = viewState.loading)
        }
    }
}