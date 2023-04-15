package github.leavesczy.compose_chat.ui.friendship

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.friendship.logic.FriendProfilePageAction
import github.leavesczy.compose_chat.ui.friendship.logic.FriendProfileViewModel
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class FriendProfileActivity : BaseActivity() {

    companion object {

        private const val keyFriendId = "keyFriendId"

        fun navTo(
            context: Context, friendId: String
        ) {
            val intent = Intent(
                context, FriendProfileActivity::class.java
            )
            intent.putExtra(
                keyFriendId, friendId
            )
            context.startActivity(intent)
        }

    }

    private val friendId by lazy {
        intent.getStringExtra(keyFriendId) ?: ""
    }

    private val friendProfileViewModel by viewModelsInstance(create = {
        FriendProfileViewModel(friendId = friendId)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FriendProfilePage(friendProfileViewModel = friendProfileViewModel)
        }
        initEvent()
    }

    private fun initEvent() {
        lifecycleScope.launch {
            friendProfileViewModel.friendProfilePageAction.collect {
                when (it) {
                    FriendProfilePageAction.NavToChat -> {
                        ChatActivity.navTo(
                            context = this@FriendProfileActivity,
                            chat = Chat.PrivateChat(id = friendId)
                        )
                        finish()
                    }
                    FriendProfilePageAction.FinishActivity -> {
                        finish()
                    }
                }
            }
        }
    }

}