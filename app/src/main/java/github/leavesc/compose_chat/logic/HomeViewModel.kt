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
import kotlinx.coroutines.withContext

/**
 * @Author: leavesC
 * @Date: 2021/6/27 14:24
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class HomeViewModel : ViewModel() {

    val appTheme = MutableStateFlow(AppThemeCache.currentTheme)

    val conversationList = ComposeChat.conversationProvider.conversationList

    val totalUnreadCount = ComposeChat.conversationProvider.totalUnreadCount

    val fiendList = ComposeChat.friendshipProvider.friendList

    val joinedGroupList = ComposeChat.groupProvider.joinedGroupList

    val personProfile = ComposeChat.accountProvider.personProfile

    val serverConnectState = ComposeChat.accountProvider.serverConnectState

    init {
        ComposeChat.conversationProvider.getConversationList()
        ComposeChat.groupProvider.getJoinedGroupList()
        ComposeChat.friendshipProvider.getFriendList()
        ComposeChat.accountProvider.refreshPersonProfile()
    }

    fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result =
                ComposeChat.conversationProvider.deleteConversation(conversation = conversation)) {
                is ActionResult.Success -> {
                    ComposeChat.conversationProvider.getConversationList()
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    fun pinConversation(conversation: Conversation, pin: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            ComposeChat.conversationProvider.pinConversation(conversation = conversation, pin = pin)
        }
    }

    fun updateProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val updateProfile = ComposeChat.accountProvider.updatePersonProfile(
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
            when (val result = ComposeChat.friendshipProvider.addFriend(friendId = formatUserId)) {
                is ActionResult.Success -> {
                    showToast("添加成功")
                }
                is ActionResult.Failed -> {
                    showToast(result.reason)
                }
            }
        }
    }

    suspend fun joinGroup(groupId: String): ActionResult {
        return withContext(Dispatchers.Main) {
            ComposeChat.groupProvider.joinGroup(groupId)
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.Main) {
            when (val result = ComposeChat.accountProvider.logout()) {
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