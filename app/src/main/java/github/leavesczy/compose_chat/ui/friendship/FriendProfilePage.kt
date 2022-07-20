package github.leavesczy.compose_chat.ui.friendship

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.common.model.PersonProfile
import github.leavesczy.compose_chat.model.FriendProfilePageAction
import github.leavesczy.compose_chat.model.FriendProfilePageViewState
import github.leavesczy.compose_chat.ui.theme.BottomSheetShape
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/7/4 1:01
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendProfilePage(
    friendProfilePageViewState: FriendProfilePageViewState,
    friendProfilePageAction: FriendProfilePageAction
) {
    val personProfile = friendProfilePageViewState.personProfile
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    fun expandSheetContent() {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = ModalBottomSheetValue.Expanded)
        }
    }

    fun hiddenSheetContent() {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = ModalBottomSheetValue.Hidden)
        }
    }

    val focusManager = LocalFocusManager.current

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = BottomSheetShape,
        sheetContent = {
            SetFriendRemarkPanel(
                friendProfile = personProfile,
                modalBottomSheetState = sheetState,
                setRemark = {
                    friendProfilePageAction.setRemark(it)
                    hiddenSheetContent()
                    focusManager.clearFocus(force = true)
                }
            )
        }
    ) {
        var openDeleteFriendDialog by remember { mutableStateOf(false) }
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            ProfilePanel(
                title = personProfile.nickname,
                subtitle = personProfile.signature,
                introduction = "ID: ${personProfile.id}" + if (personProfile.remark.isNotBlank()) {
                    "\nRemark: ${personProfile.remark}"
                } else {
                    ""
                },
                avatarUrl = personProfile.faceUrl,
                contentPadding = innerPadding,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (friendProfilePageViewState.showAlterBtb) {
                        CommonButton(text = "去聊天吧") {
                            friendProfilePageAction.navToChat()
                        }
                        CommonButton(text = "设置备注") {
                            expandSheetContent()
                        }
                        CommonButton(text = "删除好友") {
                            openDeleteFriendDialog = true
                        }
                    }
                    if (friendProfilePageViewState.showAddBtn) {
                        CommonButton(text = "加为好友") {
                            friendProfilePageAction.addFriend()
                        }
                    }
                }
            }
            if (openDeleteFriendDialog) {
                DeleteFriendDialog(
                    deleteFriend = friendProfilePageAction.deleteFriend,
                    onDismissRequest = {
                        openDeleteFriendDialog = false
                    })
            }
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
private fun SetFriendRemarkPanel(
    friendProfile: PersonProfile,
    modalBottomSheetState: ModalBottomSheetState,
    setRemark: (remark: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    fun expandSheetContent(targetValue: ModalBottomSheetValue) {
        coroutineScope.launch {
            modalBottomSheetState.animateTo(targetValue = targetValue)
        }
    }
    BackHandler(enabled = modalBottomSheetState.isVisible, onBack = {
        when (modalBottomSheetState.currentValue) {
            ModalBottomSheetValue.Hidden -> {

            }
            ModalBottomSheetValue.Expanded, ModalBottomSheetValue.HalfExpanded -> {
                expandSheetContent(targetValue = ModalBottomSheetValue.Hidden)
            }
        }
    })
    var remark by remember(key1 = friendProfile) {
        mutableStateOf(
            friendProfile.remark
        )
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.75f)
    ) {
        Column(modifier = Modifier.padding(top = 20.dp)) {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
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
                setRemark(remark)
            }
        }
    }
}