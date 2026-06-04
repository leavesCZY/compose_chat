package github.leavesczy.compose_chat.ui.friend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import github.leavesczy.compose_chat.base.BaseActivity
import github.leavesczy.compose_chat.ui.friend.logic.FriendProfileViewModel
import github.leavesczy.compose_chat.widgets.LoadingDialog

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
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
        if (friendId.isBlank()) {
            finish()
            return
        }
        setContent {
            FriendProfilePage(pageViewState = friendProfileViewModel.pageViewState)
            SetFriendRemarkDialog(viewState = friendProfileViewModel.remarkDialogViewState)
            DeleteFriendDialog(viewState = friendProfileViewModel.deleteFriendDialogViewState)
            LoadingDialog(viewState = friendProfileViewModel.loadingDialogViewState)
        }
    }

}