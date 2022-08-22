package github.leavesczy.compose_chat.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.common.model.ActionResult
import github.leavesczy.compose_chat.extend.viewModelInstance
import github.leavesczy.compose_chat.model.GroupProfilePageAction
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfileViewModel
import github.leavesczy.compose_chat.ui.friendship.FriendProfileActivity
import github.leavesczy.compose_chat.ui.main.MainActivity
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: CZY
 * @Date: 2022/7/17 14:05
 * @Desc:
 */
class GroupProfileActivity : BaseActivity() {

    companion object {

        private const val keyGroupId = "keyGroupId"

        fun navTo(context: Context, groupId: String) {
            val intent = Intent(context, GroupProfileActivity::class.java)
            intent.putExtra(keyGroupId, groupId)
            context.startActivity(intent)
        }

    }

    private val groupId by lazy {
        intent.getStringExtra(keyGroupId) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val groupProfileViewModel = viewModelInstance {
                GroupProfileViewModel(groupId = groupId)
            }
            val groupProfilePageViewState by groupProfileViewModel.groupProfilePageViewState.collectAsState()
            val groupProfilePageAction = remember {
                GroupProfilePageAction(
                    setAvatar = {
                        groupProfileViewModel.setAvatar(avatarUrl = it)
                    },
                    quitGroup = {
                        lifecycleScope.launch {
                            when (val result = groupProfileViewModel.quitGroup()) {
                                ActionResult.Success -> {
                                    showToast("已退出群聊")
                                    startActivity(
                                        Intent(
                                            this@GroupProfileActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                }
                                is ActionResult.Failed -> {
                                    showToast(result.reason)
                                }
                            }
                        }
                    },
                    onClickMember = {
                        FriendProfileActivity.navTo(context = this, friendId = it.detail.id)
                    })
            }
            ComposeChatTheme {
                GroupProfilePage(
                    viewState = groupProfilePageViewState,
                    action = groupProfilePageAction
                )
            }
        }
    }

}