package github.leavesc.compose_chat.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import github.leavesc.compose_chat.logic.ComposeChat
import github.leavesc.compose_chat.ui.weigets.CommonButton
import github.leavesc.compose_chat.ui.weigets.CommonOutlinedTextField
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/10 15:51
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun HomeMoreActionScreen(
    modalBottomSheetState: ModalBottomSheetState,
    toAddFriend: (userId: String) -> Unit,
    toJoinGroup: (groupId: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    fun expandSheetContent(targetValue: ModalBottomSheetValue) {
        coroutineScope.launch {
            modalBottomSheetState.animateTo(targetValue = targetValue)
        }
    }

    BackHandler(enabled = modalBottomSheetState.isVisible, onBack = {
        when (modalBottomSheetState.currentValue) {
            ModalBottomSheetValue.Hidden -> {

            }
            ModalBottomSheetValue.Expanded -> {
                expandSheetContent(targetValue = ModalBottomSheetValue.Hidden)
            }
            ModalBottomSheetValue.HalfExpanded -> {
                expandSheetContent(targetValue = ModalBottomSheetValue.Hidden)
            }
        }
    })

    var userId by remember(key1 = modalBottomSheetState.isVisible) {
        mutableStateOf(
            ""
        )
    }
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.8f),
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    modifier = Modifier.imePadding(),
                    backgroundColor = MaterialTheme.colors.primary,
                    elevation = 0.dp,
                    snackbarData = data,
                )
            }
        },
    ) {
        Column {
            InputItem(
                value = userId,
                onValueChange = {
                    userId = it
                },
                label = "输入 UserID",
                buttonText = "添加好友",
                onConfirm = {
                    if (userId.isBlank()) {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(message = "请输入 UserID")
                        }
                    } else {
                        toAddFriend(userId)
                    }
                }
            )
            CommonButton(text = "加入 compose_chat 交流群 - A") {
                toJoinGroup(ComposeChat.groupIdA)
            }
            CommonButton(text = "加入 compose_chat 交流群 - B") {
                toJoinGroup(ComposeChat.groupIdB)
            }
            CommonButton(text = "加入 compose_chat 交流群 - C") {
                toJoinGroup(ComposeChat.groupIdC)
            }
        }
    }
}

@Composable
private fun InputItem(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    buttonText: String,
    onConfirm: (String) -> Unit,
) {
    CommonOutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            ),
        value = value,
        onValueChange = onValueChange,
        label = label
    )
    CommonButton(text = buttonText) {
        onConfirm(value)
    }
}