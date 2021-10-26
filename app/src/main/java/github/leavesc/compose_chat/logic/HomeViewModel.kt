package github.leavesc.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.Conversation
import github.leavesc.compose_chat.cache.AccountCache
import github.leavesc.compose_chat.cache.AppThemeCache
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/6/27 14:24
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class HomeViewModel : ViewModel() {

    val appTheme = MutableStateFlow(AppThemeCache.currentTheme)

    val conversationList = Chat.conversationProvider.conversationList

    val totalUnreadCount = Chat.conversationProvider.totalUnreadCount

    val fiendList = Chat.friendshipProvider.friendList

    val personProfile = Chat.accountProvider.personProfile

    val serverConnectState = Chat.accountProvider.serverConnectState

    fun init() {
        Chat.conversationProvider.getConversationList()
        Chat.friendshipProvider.getFriendList()
        Chat.accountProvider.refreshPersonProfile()
    }

    fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result =
                Chat.conversationProvider.deleteConversation(conversation = conversation)) {
                is ActionResult.Success -> {
                    Chat.conversationProvider.getConversationList()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun pinConversation(conversation: Conversation, pin: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            Chat.conversationProvider.pinConversation(conversation = conversation, pin = pin)
        }
    }

    fun updateProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val updateProfile = Chat.accountProvider.updatePersonProfile(
                faceUrl = faceUrl,
                nickname = nickname,
                signature = signature
            )
            showToast(if (updateProfile) "更新成功" else "更新失败")
        }
    }

    fun addFriend(userId: String) {
        val formatUserId = userId.lowercase()
        viewModelScope.launch(Dispatchers.Main) {
            when (val result = Chat.friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    showToast("添加成功")
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result = Chat.accountProvider.logout()) {
                is ActionResult.Success -> {
                    AccountCache.onUserLogout()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun switchToNextTheme() {
        val nextTheme = appTheme.value.nextTheme()
        AppThemeCache.onAppThemeChanged(nextTheme)
        appTheme.value = nextTheme
    }

}