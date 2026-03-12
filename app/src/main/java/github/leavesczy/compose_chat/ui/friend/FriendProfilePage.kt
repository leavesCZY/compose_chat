package github.leavesczy.compose_chat.ui.friend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.ui.friend.logic.FriendProfilePageViewState
import github.leavesczy.compose_chat.ui.friend.logic.SetFriendRemarkDialogViewState
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.AnimatedBottomSheetDialog
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel

/**
 * @Author: leavesCZY
 * @Date: 2026/1/23 21:18
 * @Desc:
 */
@Composable
internal fun FriendProfilePage(
    pageViewState: FriendProfilePageViewState,
    openDeleteFriendDialog: () -> Unit,
    onClickChat: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            val personProfile = pageViewState.personProfile
            if (personProfile != null) {
                ProfilePanel(
                    title = personProfile.nickname,
                    subtitle = personProfile.signature,
                    introduction = "ID: ${personProfile.id}" + if (personProfile.remark.isNotBlank()) {
                        "\nRemark: ${personProfile.remark}"
                    } else {
                        ""
                    },
                    avatarUrl = personProfile.faceUrl,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(space = 16.dp)
                    ) {
                        if (!pageViewState.itIsMe) {
                            CommonButton(
                                text = "去聊天吧",
                                onClick = onClickChat
                            )
                            if (pageViewState.isFriend) {
                                CommonButton(
                                    text = "设置备注",
                                    onClick = pageViewState.showSetFriendRemarkPanel
                                )
                                CommonButton(
                                    text = "删除好友",
                                    onClick = openDeleteFriendDialog
                                )
                            } else {
                                CommonButton(
                                    text = "加为好友",
                                    onClick = pageViewState.addFriend
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DeleteFriendDialog(
    visible: Boolean,
    deleteFriend: () -> Unit,
    onDismissRequest: () -> Unit
) {
    if (visible) {
        AlertDialog(
            modifier = Modifier,
            containerColor = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF22202A.color,
            onDismissRequest = onDismissRequest,
            text = {
                Text(
                    modifier = Modifier,
                    text = "确认删除好友吗？",
                    fontSize = 17.sp,
                    lineHeight = 18.sp,
                    color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(
                        modifier = Modifier,
                        text = "取消",
                        fontSize = 15.sp,
                        lineHeight = 16.sp,
                        color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        deleteFriend()
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        text = "删除",
                        fontSize = 15.sp,
                        lineHeight = 16.sp,
                        color = ComposeChatTheme.colorScheme.c_FF001018_DEFFFFFF.color
                    )
                }
            }
        )
    }
}

@Composable
internal fun SetFriendRemarkDialog(viewState: SetFriendRemarkDialogViewState) {
    AnimatedBottomSheetDialog(
        modifier = Modifier,
        visible = viewState.visible,
        onDismissRequest = viewState.dismissDialog
    ) {
        var remark by remember(key1 = viewState.visible) {
            mutableStateOf(value = viewState.remark)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.80f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 24.dp)
        ) {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp),
                value = remark,
                onValueChange = {
                    remark = it
                },
                label = "输入备注",
            )
            CommonButton(text = "设置备注") {
                viewState.setFriendRemark(remark)
            }
        }
    }
}