package github.leavesczy.compose_chat.ui.friendship

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.model.FriendProfilePageViewState
import github.leavesczy.compose_chat.model.SetFriendRemarkDialogViewState
import github.leavesczy.compose_chat.ui.widgets.*

/**
 * @Author: leavesCZY
 * @Date: 2021/7/4 1:01
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendProfilePage(
    friendProfilePageViewState: FriendProfilePageViewState,
    setFriendRemarkDialogViewState: SetFriendRemarkDialogViewState
) {
    val personProfile = friendProfilePageViewState.personProfile
    var openDeleteFriendDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
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
                        if (friendProfilePageViewState.showAlterBtb) {
                            CommonButton(text = "去聊天吧") {
                                friendProfilePageViewState.navToChat()
                            }
                            CommonButton(text = "设置备注") {
                                friendProfilePageViewState.showSetFriendRemarkPanel()
                            }
                            CommonButton(text = "删除好友") {
                                openDeleteFriendDialog = true
                            }
                        }
                        if (friendProfilePageViewState.showAddBtn) {
                            CommonButton(text = "加为好友") {
                                friendProfilePageViewState.addFriend()
                            }
                        }
                    }
                }
                SetFriendRemarkDialog(viewState = setFriendRemarkDialogViewState)
            }
            DeleteFriendDialog(
                visible = openDeleteFriendDialog,
                deleteFriend = friendProfilePageViewState.deleteFriend,
                onDismissRequest = {
                    openDeleteFriendDialog = false
                })
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
        onClickRightButton = {
            onDismissRequest()
        })
}

@Composable
private fun SetFriendRemarkDialog(viewState: SetFriendRemarkDialogViewState) {
    ComposeBottomSheetDialog(
        visible = viewState.visible,
        onDismissRequest = viewState.onDismissRequest
    ) {
        var remark by remember(key1 = viewState.personProfile.remark) {
            mutableStateOf(
                viewState.personProfile.remark
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(fraction = 0.8f)
                .clip(shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .padding(top = 20.dp)
        ) {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),
                value = remark,
                onValueChange = {
                    remark = it
                },
                label = "输入备注",
            )
            CommonButton(text = "设置备注") {
                viewState.setRemark(remark)
            }
        }
    }
}