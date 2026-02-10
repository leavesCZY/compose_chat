package github.leavesczy.compose_chat.ui.chat.group

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.ui.MainActivity
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.group.logic.GroupProfilePageAction
import github.leavesczy.compose_chat.ui.chat.group.logic.GroupProfileViewModel
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProfileActivity : BaseActivity() {

    companion object {

        private const val KEY_GROUP_ID = "keyGroupId"

        fun navTo(context: Context, groupId: String) {
            val intent = Intent(context, GroupProfileActivity::class.java)
            intent.putExtra(KEY_GROUP_ID, groupId)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val groupId by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getStringExtra(KEY_GROUP_ID) ?: ""
    }

    private val groupProfileViewModel by viewModelsInstance {
        GroupProfileViewModel(groupId = groupId)
    }

    private val groupProfilePageAction = GroupProfilePageAction(
        setAvatar = {
            groupProfileViewModel.setAvatar(avatarUrl = it)
        },
        quitGroup = {
            lifecycleScope.launch {
                when (val result = groupProfileViewModel.quitGroup()) {
                    ActionResult.Success -> {
                        showToast(msg = "已退出群聊")
                        startActivity(Intent(this@GroupProfileActivity, MainActivity::class.java))
                    }

                    is ActionResult.Failed -> {
                        showToast(msg = result.reason)
                    }
                }
            }
        },
        onClickMember = {
            FriendProfileActivity.navTo(
                context = this,
                friendId = it.detail.id
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroupProfilePage(
                viewState = groupProfileViewModel.pageViewState,
                action = groupProfilePageAction
            )
            LoadingDialog(visible = groupProfileViewModel.loadingDialogVisible)
        }
    }

}