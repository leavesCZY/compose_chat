package github.leavesczy.compose_chat.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.conversation.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipViewModel
import github.leavesczy.compose_chat.ui.logic.MainViewModel
import github.leavesczy.compose_chat.ui.login.LoginActivity
import github.leavesczy.compose_chat.ui.person.logic.PersonProfileViewModel
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Date: 2026/5/20 17:18
 * @Desc:
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
            LoadingDialog(viewState = mainViewModel.loadingDialogViewState)
        }
        initEvent()
    }

    private fun initEvent() {
        lifecycleScope.launch {
            mainViewModel.serverConnectState.collect { state ->
                when (state) {
                    ServerConnectState.KickedOffline -> {
                        showToast(resId = R.string.toast_kicked_offline)
                        AccountProvider.onUserLogout()
                        navToLoginPage()
                    }

                    ServerConnectState.Logout,
                    ServerConnectState.UserSigExpired -> {
                        navToLoginPage()
                    }

                    ServerConnectState.Idle,
                    ServerConnectState.Connecting,
                    ServerConnectState.Connected,
                    ServerConnectState.ConnectFailed -> {
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