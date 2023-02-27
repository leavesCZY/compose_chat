package github.leavesczy.compose_chat.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.base.model.ServerState
import github.leavesczy.compose_chat.provider.AccountProvider
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.login.LoginActivity
import github.leavesczy.compose_chat.ui.main.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.main.logic.FriendshipViewModel
import github.leavesczy.compose_chat.ui.main.logic.MainViewModel
import github.leavesczy.compose_chat.ui.main.logic.PersonProfileViewModel
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog
import github.leavesczy.compose_chat.utils.showToast
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
            Box(modifier = Modifier.fillMaxSize()) {
                MainPage(
                    mainViewModel = mainViewModel,
                    conversationViewModel = conversationViewModel,
                    friendshipViewModel = friendshipViewModel,
                    personProfileViewModel = personProfileViewModel
                )
                LoadingDialog(visible = mainViewModel.loadingDialogVisible)
            }
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