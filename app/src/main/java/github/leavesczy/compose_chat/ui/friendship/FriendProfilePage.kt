package github.leavesczy.compose_chat.ui.friendship

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.ui.friendship.logic.FriendProfileViewModel
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.ComposeBottomSheetDialog
import github.leavesczy.compose_chat.ui.widgets.MessageDialog
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendProfilePage(friendProfileViewModel: FriendProfileViewModel) {
    val friendProfilePageViewState = friendProfileViewModel.friendProfilePageState
    if (friendProfilePageViewState != null) {
        val personProfile = friendProfilePageViewState.personProfile
        var openDeleteFriendDialog by remember {
            mutableStateOf(value = false)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.navigationBars
        ) { innerPadding ->
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = innerPadding)
                ) {
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (friendProfilePageViewState.isFriend) {
                                CommonButton(text = "去聊天吧") {
                                    friendProfileViewModel.navToChat()
                                }
                                CommonButton(text = "设置备注") {
                                    friendProfileViewModel.showSetFriendRemarkPanel()
                                }
                                CommonButton(text = "删除好友") {
                                    openDeleteFriendDialog = true
                                }
                            } else if (!friendProfilePageViewState.itIsMe) {
                                CommonButton(text = "加为好友") {
                                    friendProfileViewModel.addFriend()
                                }
                            }
                        }
                    }
                    SetFriendRemarkDialog(friendProfileViewModel = friendProfileViewModel)
                }
                DeleteFriendDialog(
                    visible = openDeleteFriendDialog,
                    deleteFriend = {
                        friendProfileViewModel.deleteFriend()
                    },
                    onDismissRequest = {
                        openDeleteFriendDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DeleteFriendDialog(
    visible: Boolean,
    deleteFriend: () -> Unit,
    onDismissRequest: () -> Unit
) {
    MessageDialog(
        visible = visible,
        title = "确认删除好友吗？",
        leftButtonText = "删除",
        rightButtonText = "取消",
        onClickLeftButton = {
            onDismissRequest()
            deleteFriend()
        },
        onClickRightButton = onDismissRequest
    )
}

@Composable
private fun SetFriendRemarkDialog(friendProfileViewModel: FriendProfileViewModel) {
    val viewState = friendProfileViewModel.setFriendRemarkDialogViewState
    if (viewState != null) {
        ComposeBottomSheetDialog(
            visible = viewState.visible,
            onDismissRequest = friendProfileViewModel::dismissSetFriendRemarkDialog
        ) {
            var remark by remember(key1 = viewState.visible) {
                mutableStateOf(value = viewState.personProfile.remark)
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight(fraction = 0.8f)
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
                        .padding(
                            horizontal = 20.dp, vertical = 10.dp
                        ),
                    value = remark,
                    onValueChange = {
                        remark = it
                    },
                    label = "输入备注",
                )
                CommonButton(text = "设置备注") {
                    friendProfileViewModel.setFriendRemark(remark = remark)
                }
            }
        }
    }
}