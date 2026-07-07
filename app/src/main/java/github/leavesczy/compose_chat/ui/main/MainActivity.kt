package github.leavesczy.compose_chat.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.base.BaseActivity
import github.leavesczy.compose_chat.base.models.ServerConnectState
import github.leavesczy.compose_chat.ui.login.LoginActivity
import github.leavesczy.compose_chat.ui.main.logic.MainViewModel
import github.leavesczy.compose_chat.ui.provider.LoginPreferences
import github.leavesczy.compose_chat.widgets.LoadingDialog
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainPage(
                drawerViewState = mainViewModel.drawerViewState,
                topBarViewState = mainViewModel.topBarViewState,
                bottomBarViewState = mainViewModel.bottomBarViewState,
                conversationPageViewState = mainViewModel.conversationPageViewState,
                friendshipPageViewState = mainViewModel.friendshipPageViewState,
                friendshipDialogViewState = mainViewModel.friendshipDialogViewState,
                personProfilePageViewState = mainViewModel.profilePageViewState
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
                        LoginPreferences.onUserLogout()
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