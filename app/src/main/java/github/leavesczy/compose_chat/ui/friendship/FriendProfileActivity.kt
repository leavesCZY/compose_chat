package github.leavesczy.compose_chat.ui.friendship

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import github.leavesczy.compose_chat.common.model.PrivateChat
import github.leavesczy.compose_chat.extend.viewModelsInstance
import github.leavesczy.compose_chat.model.FriendProfilePageAction
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.friendship.logic.FriendProfileViewModel
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: CZY
 * @Date: 2022/7/17 14:03
 * @Desc:
 */
class FriendProfileActivity : BaseActivity() {

    companion object {

        private const val keyFriendId = "keyFriendId"

        fun navTo(context: Context, friendId: String) {
            val intent = Intent(context, FriendProfileActivity::class.java)
            intent.putExtra(keyFriendId, friendId)
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
            val friendProfilePageViewState by friendProfileViewModel.friendProfilePageState.collectAsState()
            val friendProfilePageAction = remember {
                FriendProfilePageAction(navToChat = {
                    ChatActivity.navTo(context = this, chat = PrivateChat(id = friendId))
                }, addFriend = {
                    friendProfileViewModel.addFriend()
                }, deleteFriend = {
                    friendProfileViewModel.deleteFriend()
                    finish()
                }, setRemark = {
                    friendProfileViewModel.setFriendRemark(remark = it)
                })
            }
            ComposeChatTheme {
                FriendProfilePage(
                    friendProfilePageViewState = friendProfilePageViewState,
                    friendProfilePageAction = friendProfilePageAction
                )
            }
        }
        friendProfileViewModel.getFriendProfile()
    }

}