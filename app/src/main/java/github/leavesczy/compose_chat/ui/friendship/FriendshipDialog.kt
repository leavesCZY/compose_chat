package github.leavesczy.compose_chat.ui.friendship

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipDialogViewState
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.AnimatedBottomSheetDialog
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendshipDialog(viewState: FriendshipDialogViewState) {
    AnimatedBottomSheetDialog(
        modifier = Modifier,
        visible = viewState.visible,
        onDismissRequest = viewState.dismissDialog
    ) {
        var userId by remember {
            mutableStateOf(value = "")
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.85f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
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
            val context = LocalContext.current
            FriendshipButton(text = "添加好友") {
                if (userId.isBlank()) {
                    ToastProvider.showToast(context = context, msg = "请输入 UserID")
                } else {
                    viewState.addFriend(userId)
                }
            }
            for (groupId in viewState.groupIds) {
                FriendshipButton(text = groupId.name) {
                    viewState.joinGroup(groupId.id)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 40.dp)
            )
        }
    }
}

@Composable
private fun FriendshipButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ComposeChatTheme.colorScheme.c_FF42A5F5_FF26A69A.color),
        content = {
            Text(
                text = text,
                fontSize = 15.sp,
                lineHeight = 16.sp,
                color = ComposeChatTheme.colorScheme.c_FFFFFFFF_FFFFFFFF.color
            )
        },
        onClick = onClick
    )
}