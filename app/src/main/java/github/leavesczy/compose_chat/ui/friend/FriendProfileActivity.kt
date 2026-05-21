package github.leavesczy.compose_chat.ui.friend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.main.ChatActivity
import github.leavesczy.compose_chat.ui.friend.logic.FriendProfileViewModel
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
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

    private val friendId by lazy {
        intent.getStringExtra(KEY_FRIEND_ID) ?: ""
    }

    private val friendProfileViewModel by viewModelsInstance {
        FriendProfileViewModel(friendId = friendId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var deleteFriendDialogVisible by remember {
                mutableStateOf(value = false)
            }
            FriendProfilePage(
                pageViewState = friendProfileViewModel.pageViewState,
                openDeleteFriendDialog = {
                    deleteFriendDialogVisible = true
                },
                onClickChat = ::navToChatPage
            )
            DeleteFriendDialog(
                modifier = Modifier,
                visible = deleteFriendDialogVisible,
                deleteFriend = ::deleteFriend,
                onDismissRequest = {
                    deleteFriendDialogVisible = false
                }
            )
            SetFriendRemarkDialog(viewState = friendProfileViewModel.setFriendRemarkDialogViewState)
            LoadingDialog(viewState = friendProfileViewModel.loadingDialogViewState)
        }
    }

    private fun navToChatPage() {
        ChatActivity.navTo(
            context = this@FriendProfileActivity,
            chat = Chat.C2C(id = friendId)
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