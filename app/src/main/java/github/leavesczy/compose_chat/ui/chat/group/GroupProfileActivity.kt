package github.leavesczy.compose_chat.ui.chat.group

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import github.leavesczy.compose_chat.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.group.logic.GroupProfileViewModel
import github.leavesczy.compose_chat.widgets.LoadingDialog

/**
 * @Author: leavesCZY
 * @Date: 2026/6/4 21:12
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

    private val groupId by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getStringExtra(KEY_GROUP_ID) ?: ""
    }

    private val groupProfileViewModel by viewModelsInstance {
        GroupProfileViewModel(groupId = groupId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (groupId.isBlank()) {
            finish()
            return
        }
        setContent {
            GroupProfilePage(pageViewState = groupProfileViewModel.pageViewState)
            LoadingDialog(viewState = groupProfileViewModel.loadingDialogViewState)
        }
    }

}