package github.leavesczy.compose_chat.ui.friend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.friend.logic.FriendProfilePageViewState
import github.leavesczy.compose_chat.ui.friend.logic.FriendProfileViewModel
import github.leavesczy.compose_chat.ui.friend.logic.SetFriendRemarkDialogViewState
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.ui.widgets.AnimatedBottomSheetDialog
import github.leavesczy.compose_chat.ui.widgets.CommonButton
import github.leavesczy.compose_chat.ui.widgets.CommonOutlinedTextField
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog
import github.leavesczy.compose_chat.ui.widgets.ProfilePanel
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendProfileActivity : BaseActivity() {

    companion object {

        private const val KEY_FRIEND_ID = "keyFriendId"

        fun navTo(context: Context, friendId: String) {
            val intent = Intent(context, FriendProfileActivity::class.java)
            intent.putExtra(KEY_FRIEND_ID, friendId)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val friendId by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getStringExtra(KEY_FRIEND_ID) ?: ""
    }

    private val friendProfileViewModel by viewModelsInstance {
        FriendProfileViewModel(friendId = friendId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pageViewState = friendProfileViewModel.pageViewState
            val friendRemarkDialogViewState = friendProfileViewModel.setFriendRemarkDialogViewState
            FriendProfilePage(
                pageViewState = pageViewState,
                friendRemarkDialogViewState = friendRemarkDialogViewState,
                navToChatPage = ::navToChatPage,
                deleteFriend = ::deleteFriend
            )
            LoadingDialog(visible = friendProfileViewModel.loadingDialogVisible)
        }
    }

    private fun navToChatPage() {
        ChatActivity.navTo(
            context = this@FriendProfileActivity,
            chat = Chat.PrivateChat(id = friendId)
        )
        finish()
    }

    private fun deleteFriend() {
        lifecycleScope.launch {
            if (friendProfileViewModel.deleteFriend()) {
                finish()
            }
        }
    }

}

@Composable
private fun FriendProfilePage(
    pageViewState: FriendProfilePageViewState,
    friendRemarkDialogViewState: SetFriendRemarkDialogViewState,
    navToChatPage: () -> Unit,
    deleteFriend: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = ComposeChatTheme.colorScheme.c_FFFFFFFF_FF101010.color,
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        var openDeleteFriendDialog by remember {
            mutableStateOf(value = false)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (!pageViewState.itIsMe) {
                            CommonButton(
                                text = "去聊天吧",
                                onClick = navToChatPage
                            )
                        }
                        if (pageViewState.isFriend) {
                            CommonButton(
                                text = "设置备注",
                                onClick = pageViewState.showSetFriendRemarkPanel
                            )
                            CommonButton(text = "删除好友") {
                                openDeleteFriendDialog = true
                            }
                        } else if (!pageViewState.itIsMe) {
                            CommonButton(
                                text = "加为好友",
                                onClick = pageViewState.addFriend
                            )
                        }
                    }
                }
                SetFriendRemarkDialog(viewState = friendRemarkDialogViewState)
            }
        }
        DeleteFriendDialog(
            visible = openDeleteFriendDialog,
            deleteFriend = deleteFriend,
            onDismissRequest = {
                openDeleteFriendDialog = false
            }
        )
    }
}

@Composable
private fun DeleteFriendDialog(
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
private fun SetFriendRemarkDialog(viewState: SetFriendRemarkDialogViewState) {
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
                .fillMaxHeight(fraction = 0.75f)
        ) {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
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