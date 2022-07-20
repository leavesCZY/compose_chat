package github.leavesczy.compose_chat.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.cache.AccountCache
import github.leavesczy.compose_chat.common.model.*
import github.leavesczy.compose_chat.model.MainPageAction
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.friendship.FriendProfileActivity
import github.leavesczy.compose_chat.ui.login.LoginActivity
import github.leavesczy.compose_chat.ui.main.logic.ComposeChat
import github.leavesczy.compose_chat.ui.main.logic.MainViewModel
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme
import github.leavesczy.compose_chat.utils.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author: CZY
 * @Date: 2022/7/17 13:59
 * @Desc:
 */
class MainActivity : BaseActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val conversationPageViewState by mainViewModel.conversationPageViewState.collectAsState()
            val friendshipPageViewState by mainViewModel.friendshipPageViewState.collectAsState()
            val personProfilePageViewState by mainViewModel.personProfilePageViewState.collectAsState()
            val mainPageBottomBarViewState by mainViewModel.mainPageBottomBarViewState.collectAsState()
            val mainPageDrawerViewState by mainViewModel.mainPageDrawerViewState.collectAsState()
            val appTheme by mainViewModel.appTheme.collectAsState()
            val coroutineScope = rememberCoroutineScope()
            val localLifecycleOwner = LocalLifecycleOwner.current
            val focusManager = LocalFocusManager.current
            DisposableEffect(key1 = Unit) {
                val observer = object : DefaultLifecycleObserver {
                    override fun onStop(owner: LifecycleOwner) {
                        focusManager.clearFocus(force = false)
                    }
                }
                localLifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    localLifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            val mainPageAction = MainPageAction(
                onClickConversation = {
                    when (it) {
                        is C2CConversation -> {
                            navToChatPage(chat = PrivateChat(id = it.id))
                        }
                        is GroupConversation -> {
                            navToChatPage(chat = GroupChat(id = it.id))
                        }
                    }
                },
                onDeleteConversation = {
                    mainViewModel.deleteConversation(it)
                },
                onPinnedConversation = { conversation, pin ->
                    mainViewModel.pinConversation(conversation = conversation, pin = pin)
                },
                onClickGroupItem = {
                    navToChatPage(chat = GroupChat(id = it.id))
                },
                onClickFriendItem = {
                    navToFriendProfilePage(friendId = it.id)
                },
                joinGroup = {
                    lifecycleScope.launch {
                        when (val result = mainViewModel.joinGroup(groupId = it)) {
                            is ActionResult.Success -> {
                                delay(timeMillis = 400)
                                showToast("加入成功")
                                ComposeChat.groupProvider.getJoinedGroupList()
                            }
                            is ActionResult.Failed -> {
                                showToast(result.reason)
                            }
                        }
                    }
                },
                addFriend = {
                    lifecycleScope.launch {
                        when (val result = mainViewModel.addFriend(userId = it)) {
                            is ActionResult.Success -> {
                                delay(timeMillis = 400)
                                showToast("添加成功")
                                ChatActivity.navTo(
                                    context = this@MainActivity,
                                    chat = PrivateChat(id = it)
                                )
                            }
                            is ActionResult.Failed -> {
                                showToast(result.reason)
                            }
                        }
                    }
                },
                onTabSelected = {
                    mainViewModel.onTabSelected(tab = it)
                },
                switchToNextTheme = {
                    mainViewModel.switchToNextTheme()
                },
                logout = {
                    mainViewModel.logout()
                },
                changDrawerState = {
                    coroutineScope.launch {
                        when (it) {
                            DrawerValue.Closed -> {
                                mainPageDrawerViewState.drawerState.close()
                            }
                            DrawerValue.Open -> {
                                mainPageDrawerViewState.drawerState.open()
                            }
                        }
                    }
                }
            )
            ComposeChatTheme(appTheme = appTheme) {
                MainPage(
                    mainPageAction = mainPageAction,
                    conversationPageViewState = conversationPageViewState,
                    friendshipPageViewState = friendshipPageViewState,
                    personProfilePageViewState = personProfilePageViewState,
                    mainPageBottomBarViewState = mainPageBottomBarViewState,
                    mainPageDrawerViewState = mainPageDrawerViewState
                )
            }
        }
        initEvent()
    }

    private fun initEvent() {
        lifecycleScope.launch {
            mainViewModel.serverConnectState.collect {
                when (it) {
                    ServerState.KickedOffline -> {
                        showToast("本账号已在其它客户端登陆，请重新登陆")
                        AccountCache.onUserLogout()
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

    private fun navToChatPage(chat: Chat) {
        ChatActivity.navTo(context = this, chat = chat)
    }

    private fun navToFriendProfilePage(friendId: String) {
        FriendProfileActivity.navTo(context = this, friendId = friendId)
    }

}