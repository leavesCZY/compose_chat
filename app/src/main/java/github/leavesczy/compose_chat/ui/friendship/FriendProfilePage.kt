package github.leavesczy.compose_chat.ui.friendship

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.model.FriendProfilePageViewState
import github.leavesczy.compose_chat.model.SetFriendRemarkPanelViewState
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel

/**
 * @Author: leavesCZY
 * @Date: 2021/7/4 1:01
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendProfilePage(
    friendProfilePageViewState: FriendProfilePageViewState,
    remarkPanelViewState: SetFriendRemarkPanelViewState
) {
    val personProfile = friendProfilePageViewState.personProfile
    var openDeleteFriendDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) { innerPadding ->
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
            if (openDeleteFriendDialog) {
                DeleteFriendDialog(
                    deleteFriend = friendProfilePageViewState.deleteFriend,
                    onDismissRequest = {
                        openDeleteFriendDialog = false
                    })
            }
            SetFriendRemarkPanel(viewState = remarkPanelViewState)
        }
    }
}

@Composable
private fun DeleteFriendDialog(
    deleteFriend: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
        title = {
            Text(text = "确认删除好友吗？", style = MaterialTheme.typography.bodyLarge)
        },
        confirmButton = {
            Text(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .clickable {
                        onDismissRequest()
                        deleteFriend()
                    },
                text = "删除",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        dismissButton = {
            Text(
                modifier = Modifier
                    .padding(all = 10.dp)
                    .clickable {
                        onDismissRequest()
                    },
                text = "取消",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
    )
}

@Composable
private fun SetFriendRemarkPanel(viewState: SetFriendRemarkPanelViewState) {
    BackHandler(enabled = viewState.visible, onBack = viewState.onDismissRequest)
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .clickableNoRipple {

            },
        visible = viewState.visible,
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
        var remark by remember(key1 = viewState.personProfile.remark) {
            mutableStateOf(
                viewState.personProfile.remark
            )
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = background)) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.75f)
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
}