package github.leavesc.compose_chat.ui.friend

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.imePadding
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.extend.navigateSingleTop
import github.leavesc.compose_chat.logic.FriendProfileViewModel
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.common.CommonButton
import github.leavesc.compose_chat.ui.profile.ProfileScreen
import github.leavesc.compose_chat.ui.theme.BottomSheetShape
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/4 1:01
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendProfileScreen(
    navController: NavHostController,
    friendId: String
) {
    val friendProfileViewModel = viewModel<FriendProfileViewModel>(factory = object :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FriendProfileViewModel(friendId) as T
        }
    })
    LaunchedEffect(Unit) {
        launch {
            friendProfileViewModel.onDeleteFriendSuccess.collect {
                if (it) {
                    navController.popBackStack()
                }
            }
        }
        friendProfileViewModel.getFriendProfile()
    }
    val friendProfile by friendProfileViewModel.friendProfile.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    fun expandSheetContent() {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = ModalBottomSheetValue.Expanded)
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = BottomSheetShape,
        sheetContent = {
            SetFriendRemarkScreen(friendProfile = friendProfile,
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
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ProfileScreen(
                    personProfile = friendProfile
                )
                CommonButton(text = "去聊天吧") {
                    navController.navigateSingleTop(
                        Screen.ChatScreen(friendId = friendProfile.userId)
                    )
                }
                CommonButton(text = "设置备注") {
                    expandSheetContent()
                }
                CommonButton(text = "删除好友") {
                    openDeleteFriendDialog = true
                }
            }
            if (openDeleteFriendDialog) {
                DeleteFriendDialog(friendProfile = friendProfile, onDeleteFriend = {
                    friendProfileViewModel.deleteFriend(friendId = it)
                }, onDismissRequest = {
                    openDeleteFriendDialog = false
                })
            }
        }
    }
}

@Composable
fun DeleteFriendDialog(
    friendProfile: PersonProfile,
    onDeleteFriend: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(fraction = 0.85f),
        backgroundColor = MaterialTheme.colors.background,
        title = {
            Text(text = "确认删除好友吗？")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onDeleteFriend(friendProfile.userId)
                }
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("取消")
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetFriendRemarkScreen(
    friendProfile: PersonProfile,
    modalBottomSheetState: ModalBottomSheetState,
    onSetRemark: (userId: String, remark: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

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
                expandSheetContent(targetValue = ModalBottomSheetValue.Hidden)
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
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.8f),
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    modifier = Modifier.imePadding(),
                    backgroundColor = MaterialTheme.colors.primary,
                    elevation = 0.dp,
                    snackbarData = data,
                )
            }
        },
    ) {
        Column(modifier = Modifier) {
            Text(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp),
                text = "Set Remark",
                style = MaterialTheme.typography.subtitle1,
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),
                label = {
                    Text(text = "设置好友备注")
                },
                textStyle = MaterialTheme.typography.subtitle1,
                singleLine = true,
                maxLines = 1,
                value = remark,
                onValueChange = {
                    remark = it
                },
            )
            CommonButton(text = "设置备注") {
                onSetRemark(friendProfile.userId, remark)
            }
        }
    }
}