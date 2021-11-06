package github.leavesc.compose_chat.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.Conversation
import github.leavesc.compose_chat.cache.AccountCache
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/6/27 14:24
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class HomeViewModel : ViewModel() {

    val conversationList = ComposeChat.conversationProvider.conversationList

    val totalUnreadCount = ComposeChat.conversationProvider.totalUnreadCount

    val fiendList = ComposeChat.friendshipProvider.friendList

    val joinedGroupList = ComposeChat.groupProvider.joinedGroupList

    val personProfile = ComposeChat.accountProvider.personProfile

    init {
        ComposeChat.conversationProvider.getConversationList()
        ComposeChat.groupProvider.getJoinedGroupList()
        ComposeChat.friendshipProvider.getFriendList()
        ComposeChat.accountProvider.refreshPersonProfile()
    }

    fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            ComposeChat.conversationProvider.pinConversation(conversation = conversation, pin = pin)
        }
    }

    fun updateProfile(
        faceUrl: String,
        nickname: String,
        signature: String
    ) {
        viewModelScope.launch {
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
        viewModelScope.launch {
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
        return ComposeChat.groupProvider.joinGroup(groupId)
    }

    fun logout() {
        viewModelScope.launch {
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

}