package github.leavesczy.compose_chat.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.model.FriendshipPanelViewState
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.utils.showToast

/**
 * @Author: leavesCZY
 * @Date: 2021/7/10 15:51
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendshipPanel(viewState: FriendshipPanelViewState) {
    val visible = viewState.visible
    BackHandler(enabled = visible, onBack = viewState.onDismissRequest)
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .clickableNoRipple {

            },
        visible = visible,
        enter = slideInVertically(initialOffsetY = { 2 * it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        val background by transition.animateColor(label = "") { state ->
            if (state == EnterExitState.Visible) {
                Color(0x6D000000)
            } else {
                Color.Transparent
            }
        }
        var userId by remember(key1 = visible) {
            mutableStateOf("")
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = background)
        ) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.85f)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 20.dp, topEnd = 20.dp
                        )
                    )
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(top = 20.dp)
            ) {
                CommonOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
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
                        showToast("请输入 UserID")
                    } else {
                        viewState.addFriend(userId)
                    }
                }
                CommonButton(text = "加入交流群 0x01") {
                    viewState.joinGroup(ComposeChat.groupId01)
                }
                CommonButton(text = "加入交流群 0x02") {
                    viewState.joinGroup(ComposeChat.groupId02)
                }
                CommonButton(text = "加入交流群 0x03") {
                    viewState.joinGroup(ComposeChat.groupId03)
                }
            }
        }
    }
}