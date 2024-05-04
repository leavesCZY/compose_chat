package github.leavesczy.compose_chat.ui.friendship

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipDialogViewState
import github.leavesczy.compose_chat.ui.logic.GroupIds
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.ComposeBottomSheetDialog

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendshipDialog(viewState: FriendshipDialogViewState) {
    ComposeBottomSheetDialog(
        visible = viewState.visible.value,
        onDismissRequest = viewState.dismissDialog
    ) {
        var userId by remember {
            mutableStateOf(value = "")
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.90f)
                .clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
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
                maxLines = 1
            )
            CommonButton(text = "添加好友") {
                if (userId.isBlank()) {
                    ToastProvider.showToast(msg = "请输入 UserID")
                } else {
                    viewState.addFriend(userId)
                }
            }
            CommonButton(text = "加入交流群 0x01") {
                viewState.joinGroup(GroupIds.meetingGroupId01)
            }
            CommonButton(text = "加入交流群 0x02") {
                viewState.joinGroup(GroupIds.meetingGroupId02)
            }
            CommonButton(text = "加入交流群 0x03") {
                viewState.joinGroup(GroupIds.meetingGroupId03)
            }
            CommonButton(text = "加入交流群 0x04") {
                viewState.joinGroup(GroupIds.meetingGroupId04)
            }
            CommonButton(text = "加入交流群 0x05") {
                viewState.joinGroup(GroupIds.meetingGroupId05)
            }
            CommonButton(text = "加入交流群 0x06") {
                viewState.joinGroup(GroupIds.meetingGroupId06)
            }
        }
    }
}