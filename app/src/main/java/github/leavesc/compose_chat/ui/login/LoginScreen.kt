package github.leavesc.compose_chat.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesc.compose_chat.R
import github.leavesc.compose_chat.extend.LocalNavHostController
import github.leavesc.compose_chat.extend.navigateWithBack
import github.leavesc.compose_chat.logic.LoginViewModel
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.weigets.CommonButton
import github.leavesc.compose_chat.ui.weigets.CommonOutlinedTextField
import github.leavesc.compose_chat.ui.weigets.CommonSnackbar
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/4 1:07
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun LoginScreen() {
    val loginViewModel = viewModel<LoginViewModel>()
    val loginScreenState by loginViewModel.loginScreenState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val textInputService = LocalTextInputService.current
    val navHostController = LocalNavHostController.current
    LaunchedEffect(key1 = Unit) {
        launch {
            loginViewModel.loginScreenState.collect {
                if (it.loginSuccess) {
                    navHostController.navigateWithBack(
                        screen = Screen.HomeScreen
                    )
                    return@collect
                }
            }
        }
        loginViewModel.autoLogin()
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        scaffoldState = scaffoldState,
        snackbarHost = {
            CommonSnackbar(it)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loginScreenState.showLogo) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fraction = 0.40f)
                            .wrapContentSize(align = Alignment.Center),
                        style = MaterialTheme.typography.subtitle1,
                        fontSize = 34.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                    )
                }
                if (loginScreenState.showInput) {
                    var userId by remember { mutableStateOf(loginScreenState.lastLoginUserId) }
                    CommonOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, end = 40.dp),
                        value = userId,
                        onValueChange = { value ->
                            val realValue = value.trimStart().trimEnd()
                            if (realValue.length <= 12 && realValue.all { it.isLowerCase() || it.isUpperCase() }) {
                                userId = realValue
                            }
                        },
                        label = "UserId",
                        maxLines = 1,
                        singleLine = true
                    )
                    CommonButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, end = 40.dp, top = 30.dp),
                        text = "登陆"
                    ) {
                        if (userId.isBlank()) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(message = "请输入 UserID")
                            }
                        } else {
                            textInputService?.hideSoftwareKeyboard()
                            loginViewModel.goToLogin(userId = userId)
                        }
                    }
                }
            }
            if (loginScreenState.showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .size(size = 60.dp)
                        .wrapContentSize(align = Alignment.Center),
                    strokeWidth = 4.dp
                )
            }
        }
    }
}