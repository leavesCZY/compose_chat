package github.leavesczy.compose_chat.ui.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.login.logic.LoginPageViewState
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.theme.WindowInsetsEmpty
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog

/**
 * @Author: leavesCZY
 * @Date: 2026/1/23 21:19
 * @Desc:
 */
@Composable
internal fun LoginPage(
    viewState: LoginPageViewState,
    onClickLoginButton: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        contentWindowInsets = WindowInsetsEmpty
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
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
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 4f)
                    )
                    ComposeChat(modifier = Modifier)
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 2f)
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        content = viewState.userId,
                        onContentChange = viewState.onUserIdInputChanged,
                        tryLogin = onClickLoginButton
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 1f)
                    )
                    Login(
                        modifier = Modifier,
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
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(weight = 15f)
                    )
                }
            }
            LoadingDialog(visible = viewState.loading)
        }
    }
}

@Composable
private fun ComposeChat(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.app_name),
        style = TextStyle(
            fontSize = 62.sp,
            fontFamily = FontFamily.Cursive,
            textAlign = TextAlign.Center,
            shadow = Shadow(
                offset = Offset(5.4f, 12f),
                blurRadius = 3f
            ),
            color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
        )
    )
}

@Composable
private fun TextField(
    modifier: Modifier,
    content: TextFieldValue,
    onContentChange: (TextFieldValue) -> Unit,
    tryLogin: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .padding(horizontal = 40.dp),
        value = content,
        onValueChange = onContentChange,
        maxLines = 1,
        singleLine = true,
        label = {
            Text(
                modifier = Modifier,
                text = "UserId",
                fontSize = 14.sp,
                lineHeight = 16.sp,
                color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
            )
        },
        textStyle = TextStyle(
            fontSize = 17.sp,
            color = ComposeChatTheme.colorScheme.c_FF1C1B1F_FFFFFFFF.color
        ),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
            focusedBorderColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(
                alpha = 0.7f
            ),
            unfocusedBorderColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(
                alpha = 0.5f
            )
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = {
            tryLogin()
        })
    )
}

@Composable
private fun Login(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = 24.dp))
            .background(color = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 12.dp),
            text = "Login",
            fontSize = 15.sp,
            lineHeight = 16.sp,
            color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
        )
    }
}