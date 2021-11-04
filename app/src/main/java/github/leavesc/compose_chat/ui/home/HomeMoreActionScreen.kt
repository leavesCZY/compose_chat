package github.leavesc.compose_chat.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesc.compose_chat.logic.ComposeChat
import github.leavesc.compose_chat.ui.weigets.CommonButton
import github.leavesc.compose_chat.ui.weigets.CommonOutlinedTextField
import github.leavesc.compose_chat.ui.weigets.CommonSnackbar
import kotlinx.coroutines.Dispatchers
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
        coroutineScope.launch(Dispatchers.Main) {
            modalBottomSheetState.animateTo(targetValue = targetValue)
        }
    }

    BackHandler(enabled = modalBottomSheetState.isVisible, onBack = {
        when (modalBottomSheetState.currentValue) {
            ModalBottomSheetValue.Hidden -> {

            }
            ModalBottomSheetValue.Expanded -> {
                expandSheetContent(targetValue = ModalBottomSheetValue.HalfExpanded)
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
            CommonSnackbar(it)
        },
    ) {
        Column {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    ),
                value = userId,
                onValueChange = { id ->
                    val realValue = id.trimStart().trimEnd()
                    if (realValue.all { it.isLowerCase() || it.isUpperCase() }) {
                        userId = realValue
                    }
                },
                label = "输入 UserID",
                singleLine = true,
                maxLines = 1,
            )
            CommonButton(text = "添加好友") {
                if (userId.isBlank()) {
                    coroutineScope.launch(Dispatchers.Main) {
                        scaffoldState.snackbarHostState.showSnackbar(message = "请输入 UserID")
                    }
                } else {
                    toAddFriend(userId)
                }
            }
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