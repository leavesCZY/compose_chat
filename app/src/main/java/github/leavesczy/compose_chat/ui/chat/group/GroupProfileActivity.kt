package github.leavesczy.compose_chat.ui.chat.group

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.group.logic.GroupProfileViewModel
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
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

    private val groupProfileViewModel by viewModelsInstance {
        val groupId = intent.getStringExtra(KEY_GROUP_ID) ?: ""
        GroupProfileViewModel(groupId = groupId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroupProfilePage(viewState = groupProfileViewModel.pageViewState)
            LoadingDialog(viewState = groupProfileViewModel.loadingDialogViewState)
        }
    }

}