package github.leavesczy.compose_chat.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.base.models.ServerState
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.conversation.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipViewModel
import github.leavesczy.compose_chat.ui.logic.MainViewModel
import github.leavesczy.compose_chat.ui.login.LoginActivity
import github.leavesczy.compose_chat.ui.person.logic.PersonProfileViewModel
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainActivity : BaseActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    private val conversationViewModel by viewModels<ConversationViewModel>()

    private val friendshipViewModel by viewModels<FriendshipViewModel>()

    private val personProfileViewModel by viewModels<PersonProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainPage(
                mainViewModel = mainViewModel,
                conversationViewModel = conversationViewModel,
                friendshipViewModel = friendshipViewModel,
                personProfileViewModel = personProfileViewModel
            )
        }
        initEvent()
    }

    private fun initEvent() {
        lifecycleScope.launch {
            mainViewModel.serverConnectState.collect {
                when (it) {
                    ServerState.KickedOffline -> {
                        showToast(msg = "本账号已在其它客户端登陆，请重新登陆")
                        AccountProvider.onUserLogout()
                        navToLoginPage()
                    }

                    ServerState.Logout, ServerState.UserSigExpired -> {
                        navToLoginPage()
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun navToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}