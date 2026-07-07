package github.leavesczy.compose_chat.ui.login

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.theme.AppTheme
import github.leavesczy.compose_chat.ui.login.logic.LoginPageViewState
import github.leavesczy.compose_chat.ui.provider.ToastProvider

@Composable
internal fun LoginPage(pageViewState: LoginPageViewState) {
    val localActivity = LocalActivity.current
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = AppTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        contentWindowInsets = WindowInsets()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (pageViewState.isPanelVisible) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 3f)
                )
                Logo(modifier = Modifier)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 2f)
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    content = pageViewState.userId,
                    onContentChange = pageViewState.onUserIdInputChanged
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 2f)
                )
                LoginButton(
                    modifier = Modifier,
                    onClick = {
                        val input = pageViewState.userId.text
                        if (input.isBlank()) {
                            ToastProvider.showToast(resId = github.leavesczy.compose_chat.base.R.string.login_user_id_required)
                        } else {
                            localSoftwareKeyboardController?.hide()
                            localActivity?.let { activity ->
                                pageViewState.onClickLogin(activity)
                            }
                        }
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 16f)
                )
            }
        }
    }
}

@Composable
private fun Logo(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.app_name),
        style = TextStyle(
            fontSize = 64.sp,
            fontFamily = FontFamily.Cursive,
            textAlign = TextAlign.Center,
            shadow = Shadow(
                offset = Offset(x = 6f, y = 14f),
                blurRadius = 4f
            ),
            color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
        )
    )
}

@Composable
private fun TextField(
    modifier: Modifier,
    content: TextFieldValue,
    onContentChange: (content: TextFieldValue) -> Unit
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
                text = stringResource(id = R.string.login_user_id),
                fontSize = 14.sp,
                lineHeight = 16.sp,
                color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
            )
        },
        textStyle = TextStyle(
            fontSize = 17.sp,
            color = AppTheme.colorScheme.c_FF1C1B1F_FFFFFFFF.color
        ),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color,
            focusedBorderColor = AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(
                alpha = 0.7f
            ),
            unfocusedBorderColor = AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color.copy(
                alpha = 0.5f
            )
        )
    )
}

@Composable
private fun LoginButton(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = 24.dp))
            .background(color = AppTheme.colorScheme.c_FF42A5F5_FF26A69A.color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 12.dp),
            text = stringResource(id = R.string.login),
            fontSize = 15.sp,
            lineHeight = 16.sp,
            color = AppTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
        )
    }
}