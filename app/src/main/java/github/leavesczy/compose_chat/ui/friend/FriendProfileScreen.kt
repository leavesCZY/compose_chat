package github.leavesczy.compose_chat.ui.friend

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.extend.LocalNavHostController
import github.leavesczy.compose_chat.extend.navToC2CChatScreen
import github.leavesczy.compose_chat.extend.navToHomeScreen
import github.leavesczy.compose_chat.extend.viewModelInstance
import github.leavesczy.compose_chat.logic.FriendProfileViewModel
import github.leavesczy.compose_chat.ui.profile.ProfileScreen
import github.leavesczy.compose_chat.ui.theme.BottomSheetShape
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2021/7/4 1:01
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendProfileScreen(
    friendId: String
) {
    val friendProfileViewModel = viewModelInstance {
        FriendProfileViewModel(friendId = friendId)
    }
    LaunchedEffect(key1 = Unit) {
        friendProfileViewModel.getFriendProfile()
    }
    val friendProfileScreenState by friendProfileViewModel.friendProfileScreenState.collectAsState()
    val personProfile = friendProfileScreenState.personProfile
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val navHostController = LocalNavHostController.current

    fun expandSheetContent() {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = ModalBottomSheetValue.Expanded)
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = BottomSheetShape,
        sheetContent = {
            SetFriendRemarkScreen(friendProfile = personProfile,
                modalBottomSheetState = sheetState,
                onSetRemark = { friendId, remark ->
                    friendProfileViewModel.setFriendRemark(
                        friendId = friendId,
                        remark = remark
                    )
                })
        }
    ) {
        var openDeleteFriendDialog by remember { mutableStateOf(false) }
        Scaffold {
            ProfileScreen(
                title = personProfile.nickname,
                subtitle = personProfile.signature,
                introduction = "ID: ${personProfile.userId}" + if (personProfile.remark.isNotBlank()) {
                    "\nRemark: ${personProfile.remark}"
                } else {
                    ""
                },
                avatarUrl = personProfile.faceUrl
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (friendProfileScreenState.showAlterBtb) {
                        CommonButton(text = "去聊天吧") {
                            navHostController.popBackStack()
                            navHostController.navToC2CChatScreen(friendId = personProfile.userId)
                        }
                        CommonButton(text = "设置备注") {
                            expandSheetContent()
                        }
                        CommonButton(text = "删除好友") {
                            openDeleteFriendDialog = true
                        }
                    }
                    if (friendProfileScreenState.showAddBtn) {
                        CommonButton(text = "添加为好友") {
                            friendProfileViewModel.addFriend()
                        }
                    }
                }
            }
            if (openDeleteFriendDialog) {
                DeleteFriendDialog(friendProfile = personProfile, onDeleteFriend = {
                    friendProfileViewModel.deleteFriend(friendId = it)
                    navHostController.navToHomeScreen()
                }, onDismissRequest = {
                    openDeleteFriendDialog = false
                })
            }
        }
    }
}

@Composable
private fun DeleteFriendDialog(
    friendProfile: PersonProfile,
    onDeleteFriend: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 20.dp),
        title = {
            Text(text = "确认删除好友吗？")
        },
        confirmButton = {
            Text(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .clickable {
                        onDismissRequest()
                        onDeleteFriend(friendProfile.userId)
                    },
                text = "删除",
            )
        },
        dismissButton = {
            Text(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .clickable {
                        onDismissRequest()
                    },
                text = "取消"
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
    )
}

@Composable
private fun SetFriendRemarkScreen(
    friendProfile: PersonProfile,
    modalBottomSheetState: ModalBottomSheetState,
    onSetRemark: (userId: String, remark: String) -> Unit
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
            ModalBottomSheetValue.Expanded -> {
                expandSheetContent(targetValue = ModalBottomSheetValue.HalfExpanded)
            }
            ModalBottomSheetValue.HalfExpanded -> {
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
                label = "Set Remark",
            )
            CommonButton(text = "设置备注") {
                onSetRemark(friendProfile.userId, remark)
            }
        }
    }
}